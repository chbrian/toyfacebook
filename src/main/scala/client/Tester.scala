package client

import javax.imageio.ImageIO
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import client.Main._

import akka.actor.{Props, ActorLogging, ActorSystem, Actor}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._
/**
  * Created by xiaoyong on 11/30/2015.
  */
class Tester extends Actor with ActorLogging{


  val host = "http://localhost:8080/"

  val alphabet = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')

  def randomString(length: Int) = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(alphabet(Random.nextInt(62)))
    }
    sb.toString
  }

  implicit val timeout: Timeout = 60.seconds

  val system = context.system

  // user test
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

  // post test
  def createPost(userId: String, content: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "post", facebook.Structures.Post(userId, content)))
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

  // album test
  def createAlbum(userId: String, name: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "album", facebook.Structures.Album(userId, name)))
    response
  }

  def getAlbum(albumId: Int): Future[facebook.Structures.Album] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[facebook.Structures.Album] = sendReceive ~> unmarshal[facebook.Structures.Album]

    val response: Future[facebook.Structures.Album] = pipeline(Get(host + "album/" + albumId))
    response
  }

  def deleteAlbum(albumId: Int): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Delete(host + "album/" + albumId))
    response

  }


  // picture test
  def createPicture(albumId: Int, name: String, location: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val source = Source.fromFile(location, "ISO-8859-1")
    val content = source.map(_.toByte).to[ArrayBuffer]
    val response: Future[HttpResponse] = pipeline(Post(host + "picture", facebook.Structures.Picture(albumId, name, content)))

    response

  }


  def getPicture(pictureId: Int): Future[facebook.Structures.Picture] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[facebook.Structures.Picture] = sendReceive ~> unmarshal[facebook.Structures.Picture]

    val response: Future[facebook.Structures.Picture] = pipeline(Get(host + "picture/" + pictureId))

    response
  }

  def deletePicture(pictureId: Int): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Delete(host + "picture/" + pictureId))
    response

  }

  // group test

  def createGroup(id: String, userId: String, name: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "group", facebook.Structures.Group(id, userId, name)))
    response
  }

  def getGroup(id: String): Future[facebook.Structures.Group] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[facebook.Structures.Group] = sendReceive ~> unmarshal[facebook.Structures.Group]

    val response: Future[facebook.Structures.Group] = pipeline(Get(host + "group/" + id))
    response
  }

  def deleteGroup(id: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Delete(host + "group/" + id))
    response

  }


  def receive = {

    case CreateUser(id: String) =>
      import system.dispatcher
      createUser(id, randomString(Random.nextInt(10)), randomString(Random.nextInt(5))) onComplete {
        case Success(response) => log.info("Create user {}, received response: {}", id, response.status)
        case Failure(error) => log.warning("Create user {} request error: {}", id, error.getMessage)
      }

    case GetUser(id: String) =>
      import system.dispatcher
      getUser(id) onComplete {
        case Success(response) => log.info("Get user {}, received response: {}", id, response)
        case Failure(error) => log.warning("Get user {} request error: {}", id, error.getMessage)
      }

    case DeleteUser(id: String) =>
      import system.dispatcher
      deleteUser(id) onComplete {
        case Success(response) => log.info("Delete user {}, received response: {}", id, response.status)
        case Failure(error) => log.warning("Delete user {} request error: {}", id, error.getMessage)
      }

    case CreatePost(userId: String) =>
      import system.dispatcher
      // create group with the same name with its owner
      createPost(userId, randomString(Random.nextInt(10))) onComplete {
        case Success(response) => log.info("Create post {}, received response: {}", userId, response.status)
        case Failure(error) => log.warning("Create post {} request error: {}", userId, error.getMessage)
      }

    case GetPost(postId: Int) =>
      import system.dispatcher
      // create group with the same name with its owner
      getPost(postId) onComplete {
        case Success(response) => log.info("Get post {}, received response: {}", postId, response)
        case Failure(error) => log.warning("Get post {} request error: {}", postId, error.getMessage)
      }

    case DeletePost(postId: Int) =>
      import system.dispatcher
      deletePost(postId) onComplete {
        case Success(response) => log.info("Delete group {}, received response: {}", postId, response.status)
        case Failure(error) => log.warning("Delete group {} request error: {}", postId, error.getMessage)
      }

    case CreateAlbum(userId: String) =>
      import system.dispatcher
      // create group with the same name with its owner
      createAlbum(userId, randomString(Random.nextInt(10))) onComplete {
        case Success(response) => log.info("Create album {}, received response: {}", userId, response.status)
        case Failure(error) => log.warning("Create album {} request error: {}", userId, error.getMessage)
      }

    case GetAlbum(albumId: Int) =>
      import system.dispatcher
      getAlbum(albumId) onComplete {
        case Success(response) => log.info("Get album {}, received response: {}", albumId, response)
        case Failure(error) => log.warning("Get album {} request error: {}", albumId, error.getMessage)
      }

    case DeleteAlbum(albumId: Int) =>
      import system.dispatcher
      deleteAlbum(albumId) onComplete {
        case Success(response) => log.info("Delete album {}, received response: {}", albumId, response.status)
        case Failure(error) => log.warning("Delete album {} request error: {}", albumId, error.getMessage)
      }

    case CreatePicture(albumId: Int, location: String) =>
      import system.dispatcher
      // create group with the same name with its owner
      createPicture(albumId, randomString(Random.nextInt(10)), location) onComplete {
        case Success(response) => log.info("Create picture {}, received response: {}", location, response.status)
        case Failure(error) => log.warning("Create picture {} request error: {}", location, error.getMessage)
      }

    case GetPicture(pictureId: Int) =>
      import system.dispatcher
      getPicture(pictureId) onComplete {
        case Success(response) => log.info("Get picture {}, received response: {}", pictureId, response.name) // change response here
        case Failure(error) => log.warning("Get picture {} request error: {}", pictureId, error.getMessage)
      }

    case DeletePicture(pictureId: Int) =>
      import system.dispatcher
      deletePicture(pictureId) onComplete {
        case Success(response) => log.info("Delete picture {}, received response: {}", pictureId, response.status)
        case Failure(error) => log.warning("Delete picture {} request error: {}", pictureId, error.getMessage)
      }

    case CreateGroup(id: String) =>
      import system.dispatcher
      // create group with the same name with its owner
      createGroup(id, id, randomString(Random.nextInt(5))) onComplete {
        case Success(response) => log.info("Create group {}, received response: {}", id, response.status)
        case Failure(error) => log.warning("Create group {} request error: {}", id, error.getMessage)
      }

    case GetGroup(id: String) =>
      import system.dispatcher
      getGroup(id) onComplete {
        case Success(response) => log.info("Get group {}, received response: {}", id, response)
        case Failure(error) => log.warning("Get group {} request error: {}", id, error.getMessage)
      }

    case DeleteGroup(id: String) =>
      import system.dispatcher
      deleteGroup(id) onComplete {
        case Success(response) => log.info("Delete group {}, received response: {}", id, response)
        case Failure(error) => log.warning("Delete group {} request error: {}", id, error.getMessage)
      }
  }
}
