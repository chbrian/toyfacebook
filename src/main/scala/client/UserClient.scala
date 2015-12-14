package client

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.{Cipher, KeyGenerator}

import akka.actor.{ActorLogging, Actor}
import akka.util.Timeout

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Random}
import scala.collection.mutable

import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._

import facebook.Structures.NestedJsonProtocol._

import client.Main._

/**
  * Created by xiaoyong on 12/12/2015.
  */

class UserClient extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  val alphabet = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')

  def randomString(length: Int): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(alphabet(Random.nextInt(62)))
    }
    sb.toString
  }

  def randomTime(): String = {
    val sb = new StringBuilder
    sb.append(Random.nextInt(12) + "/") // month
    sb.append(Random.nextInt(30) + "/") // day
    sb.append(Random.nextInt(100) + " ") // year
    sb.toString()
  }

  implicit val timeout: Timeout = 60.seconds


  var userId = new String()
  val system = context.system

  // User Key
  val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
  val keyGen = KeyPairGenerator.getInstance("RSA")
  keyGen.initialize(1024, random)
  val pairKey = keyGen.generateKeyPair
  val privateKey = pairKey.getPrivate
  val publicKey = pairKey.getPublic

  def createUser(id: String, name: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Post(host + "user",
      facebook.Structures.User(id, name, publicKey.getEncoded)))
    response
  }

  def getUser(id: String): Future[facebook.Structures.User] = {
    val pipeline: HttpRequest => Future[facebook.Structures.User] = sendReceive ~> unmarshal[facebook.Structures.User]
    val response: Future[facebook.Structures.User] = pipeline(Get(host + "user/" + id))
    response
  }

  def deleteUser(id: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Delete(host + "user/" + id))
    response

  }

  def addFriend(id1: String, id2: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Put(host + "user/addFriend/" + id1 + "/" + id2))
    response
  }

  def removeFriend(id1: String, id2: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Delete(host + "user/removeFriend/" + id1 + "/" + id2))
    response
  }

  // post test
  def createPost(userId: String, content: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val symKey = KeyGenerator.getInstance("AES").generateKey()
    val encryptedContent = encryptMsg(content, privateKey, publicKey, symKey)
    val publicKeyMap = mutable.Map[String, Array[Byte]](userId -> publicKey.getEncoded)
    val result = Await.result(getFriendPublicKey, timeout.duration)
    publicKeyMap ++= result
//    getFriendPublicKey onComplete {
//      case Success(response) =>
//        publicKeyMap ++= response
//      case Failure(error) => log.warning("Get Friends' public key, request error: {}",  error.getMessage)
//    }
    val userKeyMap = encryptKey(symKey, publicKeyMap)
    val response: Future[HttpResponse] = pipeline(Post(host + "post",
      facebook.Structures.Post(userId, encryptedContent,
        userKeyMap.keys.to[ArrayBuffer], userKeyMap.values.to[ArrayBuffer])))
    response
  }

  def getPost(postId: Int): Future[facebook.Structures.Post] = {
    val pipeline: HttpRequest => Future[facebook.Structures.Post] = sendReceive ~> unmarshal[facebook.Structures.Post]
    val response: Future[facebook.Structures.Post] = pipeline(Get(host + "post/" + postId))
    response
  }

  def getPostContent(post: facebook.Structures.Post): Option[String] = {
    val index = post.userList.indexOf(userId)
    println("userListLength: "+post.userList.length)
    println("head: "+post.userList(1))
    if (index >= 0) {
      val encryptedKey = post.keyList(index)
      val symKey = decryptKey(encryptedKey, privateKey)
      val content = decryptMsg(post.encryptedContent, symKey)
      println(content)
      content
    }
    else {
      None
    }

  }

  def deletePost(postId: Int): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Delete(host + "post/" + postId))
    response
  }

  def encryptMsg(msg: String, myPrivateKey: PrivateKey, myPublicKey: PublicKey, symKey: Key): Array[Byte] = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg.getBytes("UTF-8"))
    val digest = md.digest()

    val dsaPrivate = Signature.getInstance("SHA256withRSA")
    dsaPrivate.initSign(myPrivateKey)
    dsaPrivate.update(digest)
    val signature = dsaPrivate.sign
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, symKey)
    cipher.doFinal(msg.getBytes ++ signature ++ myPublicKey.getEncoded)
  }

  def encryptKey(symKey: Key, publicKeyMap: mutable.Map[String, Array[Byte]]): mutable.Map[String, Array[Byte]] = {
    val symKeyMap = mutable.Map[String, Array[Byte]]()
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    for (user <- publicKeyMap.keys) {
      val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyMap(user)))
      cipher.init(Cipher.WRAP_MODE, publicKey)
      symKeyMap(user) = cipher.wrap(symKey)
      println("userId: "+userId+", encrypted user "+user)
    }
    symKeyMap
  }

  def decryptKey(encryptKey: Array[Byte], myPrivateKey: PrivateKey): Key = {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.UNWRAP_MODE, myPrivateKey)
    val symK = cipher.unwrap(encryptKey, "AES", Cipher.SECRET_KEY)
    symK
  }

  def decryptMsg(encryptedMsg: Array[Byte], symKey: Key): Option[String] = {

    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, symKey)
    val tmp = cipher.doFinal(encryptedMsg)
    val tmp2 = tmp.take(tmp.length - 162)
    val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(tmp.takeRight(162)))

    val msg = tmp2.take(tmp2.length - 128)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg)
    val digest = md.digest()

    val dsaPublic = Signature.getInstance("SHA256withRSA")
    dsaPublic.initVerify(publicKey)
    dsaPublic.update(digest)
    if (dsaPublic.verify(tmp2.takeRight(128)))
      Some(new String(msg, "UTF-8"))
    else
      None
  }

  def getFriendPublicKey: Future[Map[String, Array[Byte]]] = {
    val pipeline: HttpRequest => Future[Map[String, Array[Byte]]] = sendReceive ~> unmarshal[Map[String, Array[Byte]]]
    val response: Future[Map[String, Array[Byte]]] = pipeline(Get(host + "friendKey/" + userId))
    response
  }

  def receive = {
    case CreateUser(userId: String) =>
      this.userId = userId
      createUser(userId, randomString(7)) onComplete {
        case Success(response) => log.info("Create user {}, received response: {}", userId, response.status)
        case Failure(error) => log.warning("Create user {} request error: {}", userId, error.getMessage)
      }
    case GetUser(id: String) =>
      getUser(id) onComplete {
        case Success(response) => log.info("Get user {}, received response: {}", id, response)
        case Failure(error) => log.warning("Get user {} request error: {}", id, error.getMessage)
      }
    case AddFriend(id: String) =>
      addFriend(userId, id) onComplete {
        case Success(response) => log.info("Add user {} with friend {}, received response: {}", userId, id, response.status)
        case Failure(error) => log.warning("Add user {} with friend {} request error: {}", userId, id, error.getMessage)
      }
    case CreatePost(content: String) =>
      // create group with the same name with its owner
      createPost(userId, content) onComplete {
        case Success(response) => log.info("Create post {}, received response: {}", userId, response.status)
        case Failure(error) => log.warning("Create post {} request error: {}", userId, error.getMessage)
      }
    case GetPost(postId: Int) =>
      // create group with the same name with its owner
      getPost(postId) onComplete {
        case Success(response) => log.info("Get post {}, received response: {}", postId, getPostContent(response))
//          postId, getPostContent(response.asInstanceOf[facebook.Structures.Post]))
        case Failure(error) => log.warning("Get post {} request error: {}", postId, error.getMessage)
      }

  }

}
