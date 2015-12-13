package client

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.{Cipher, KeyGenerator}

import akka.actor.{ActorLogging, Actor}
import akka.util.Timeout
import spray.httpx.unmarshalling.Deserializer

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Random}
import scala.collection.mutable

import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._


/**
  * Created by xiaoyong on 12/12/2015.
  */

class UserClient extends Actor with ActorLogging {

  val host = "http://localhost:8080/"
  val userId = "abc"

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

  // User Key
  val system = context.system
  val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
  val keyGen = KeyPairGenerator.getInstance("RSA")
  keyGen.initialize(1024, random)
  val pairKey = keyGen.generateKeyPair()
  val privateKey = pairKey.getPrivate()
  val publicKey = pairKey.getPublic()


  def createUser(id: String, name: String, password: String): Future[HttpResponse] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Post(host + "user", facebook.Structures.User(id, name, password)))
    response
  }

  def getUser(id: String): Future[facebook.Structures.User] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[facebook.Structures.User] = sendReceive ~> unmarshal[facebook.Structures.User]
    val response: Future[facebook.Structures.User] = pipeline(Get(host + "user/" + id))
    response
  }

  def deleteUser(id: String): Future[HttpResponse] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Delete(host + "user/" + id))
    response

  }

  def addFriend(id1: String, id2: String): Future[HttpResponse] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Put(host + "user/addFriend/" + id1 + "/" + id2))
    response
  }

  def removeFriend(id1: String, id2: String): Future[HttpResponse] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Delete(host + "user/removeFriend/" + id1 + "/" + id2))
    response
  }

  // post test
  def createPost(userId: String, content: String): Future[HttpResponse] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val symKey = KeyGenerator.getInstance("AES").generateKey()
    val encryptedContent = encryptMsg(content, privateKey, publicKey, symKey)
    val friendKeyMap = getFriendPublicKey()
    val publicKeyMap = mutable.Map[String, Array[Byte]](userId -> publicKey.getEncoded) ++ friendKeyMap
    // TODO
    val userKeyMap = encryptKey(symKey, publicKeyMap)
    val response: Future[HttpResponse] = pipeline(Post(host + "post",
      facebook.Structures.Post(userId, encryptedContent, userKeyMap)))
    response
  }

  def getPost(postId: Int): Future[facebook.Structures.Post] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[facebook.Structures.Post] = sendReceive ~> unmarshal[facebook.Structures.Post]
    val response: Future[facebook.Structures.Post] = pipeline(Get(host + "post/" + postId))
    response
  }

  def deletePost(postId: Int): Future[HttpResponse] = {
    import system.dispatcher
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
    println("signature " + signature)
    val cipher1 = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher1.init(Cipher.ENCRYPT_MODE, symKey)
    cipher1.doFinal(msg.getBytes ++ signature ++ myPublicKey.getEncoded)
  }

  def encryptKey(symKey: Key, publicKeyMap: mutable.Map[String, Array[Byte]]): mutable.Map[String, Array[Byte]] = {
    val symKeyMap = mutable.Map[String, Array[Byte]]()
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    for (user <- publicKey) {
      val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyMap(user)))
      cipher.init(Cipher.WRAP_MODE, publicKey)
      symKeyMap(user) = cipher.wrap(symKey)
    }
    symKeyMap
  }

  def decrypt(encryptedMsg: Array[Byte], myPrivateKey: PrivateKey, myPublicKey: PublicKey): Option[String] = {
    val tmp1 = encryptedMsg.take(encryptedMsg.length - 128)
    val tmp2 = encryptedMsg.takeRight(128)

    val cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher1.init(Cipher.UNWRAP_MODE, myPrivateKey)
    val symK = cipher1.unwrap(tmp2, "AES", Cipher.SECRET_KEY)

    val cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher2.init(Cipher.DECRYPT_MODE, symK)
    val tmp3 = cipher2.doFinal(tmp1)
    val tmp4 = tmp3.take(tmp3.length - 162)
    val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(tmp3.takeRight(162)))

    val msg = tmp4.take(tmp4.length - 128)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg)
    val digest = md.digest()

    val dsaPublic = Signature.getInstance("SHA256withRSA")
    dsaPublic.initVerify(publicKey)
    dsaPublic.update(digest)
    if (dsaPublic.verify(tmp4.takeRight(128)))
      Some(new String(msg, "UTF-8"))
    else
      None
  }

  def getFriendPublicKey(): Option[Map[String, Array[Byte]]] = {
    import system.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Get(host + "friendKey/" + userId))
    response onComplete {
      case Success(response) => response
      case Failure(error) => log.error("Can't get friends' public key ", userId, error.getMessage)
        None
    }
  }

  def receive = {

  }

}
