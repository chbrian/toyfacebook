package client

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import facebook.Structures._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Requests made by the client
  * Created by alan on 11/24/2015.
  * Modified by xiaohui on 11/27/2015.
  * Modified by xiaoyong on 11/29/2015.
  */
trait Requests {
  private implicit val timeout: Timeout = 2.seconds

  import spray.httpx.RequestBuilding._

  //Request about create/get/deleteUser

  def createUser(host: String, id: String, name: String, password: String)(implicit system: ActorSystem): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "user", User(id, name, password)))
    response
  }

  def getUser(host: String, id: String)(implicit system: ActorSystem): Future[User] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[User] = sendReceive ~> unmarshal[User]

    val response: Future[User] = pipeline(Get(host + "user/" + id))
    response
  }

  def deleteUser(host: String, id: String)(implicit system: ActorSystem): Future[String] = {
    import system.dispatcher
    for {
      response <- (IO(Http) ? Delete(host + "user/" + id)).mapTo[HttpResponse]
    } yield {
      response.status.toString
    }
  }

  //
  //  //Request about create/get/removePost
  //
  //  def createPost(host: String, id: String, content: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "post/", Post(id, content)))
  //  }
  //
  //  def getPost(host: String, postId: Int)(implicit system: ActorSystem): Future[Post] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Post] = sendReceive ~> unmarshal[Post]
  //
  //    val response: Future[Post] = pipeline(Get(host + "post/" + postId))
  //    response
  //  }
  //
  //  def removePost(host: String, id: String, postId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "post/" + postId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about add/removeFriend
  //
  //  def addFriend(host: String, id: String, friendId: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Put(host + "friend/" + friendId))
  //  }
  //
  //  def removeFriend(host: String, id: String, friendId: String)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "friend/" + friendId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about create/get/deleteProfile
  //
  //  def createProfile(host: String, fbType: String, fbId: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "profile", Profile(fbType, fbId)))
  //  }
  //
  //  def getProfile(host: String, profileId: Int)(implicit system: ActorSystem): Future[Profile] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Profile] = sendReceive ~> unmarshal[Profile]
  //
  //    val response: Future[Profile] = pipeline(Get(host + "profile/" + profileId))
  //    response
  //  }
  //
  //  def deleteProfile(host: String, profileId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "profile/" + profileId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about create/get/deletePicture
  //
  //  def createPicture(host: String, albumId: Int, name: String, contentPath: String)(implicit system: ActorSystem):
  //  Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val source = scala.io.Source.fromFile(contentPath)
  //    val content = source.map(_.toByte).to[ArrayBuffer]
  //    source.close()
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "picture", Picture(albumId, name, content)))
  //    response
  //  }
  //
  //  def getPicture(host: String, id: Int)(implicit system: ActorSystem): Future[Picture] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Picture] = sendReceive ~> unmarshal[Picture]
  //
  //    val response: Future[Picture] = pipeline(Get(host + "picture" + id))
  //    response
  //  }
  //
  //  def deletePicture(host: String, pictureId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "picture/" + pictureId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about create/get/removeAlbum
  //
  //  def createAlbum(host: String, userId: String, name: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "album/" + Album(userId, name)))
  //  }
  //
  //  def getAlbum(host: String, albumId: Int)(implicit system: ActorSystem): Future[Album] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Album] = sendReceive ~> unmarshal[Album]
  //
  //    val response: Future[Album] = pipeline(Get(host + "album/" + albumId))
  //    response
  //  }
  //
  //  def removeAlbum(host: String, userId: String, albumId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "album/" + albumId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about Group
  //
  //  def createGroup(host: String, group: Group)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "group", Group(group)))
  //  }
  //
  //  def getGroup(host: String, groupId: String)(implicit system: ActorSystem): Future[Group] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Group] = sendReceive ~> unmarshal[Group]
  //
  //    val response: Future[Group] = pipeline(Get(host + "group/" + groupId))
  //    response
  //  }
  //
  //  def deleteGroup(host: String, groupId: String)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "group/" + groupId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  def joinUserGroup(host: String, userId: String, groupId: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Put(host + "group/" + groupId))
  //  }
  //
  //  def leaveUserGroup(host: String, userId: String, groupId: String)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "group/" + groupId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //
  //   def addAlbumGroup(host: String, albumId: Int, groupId: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Put(host + "album/" + albumId + "group/" + groupId))
  //  }
  //
  //  def removeAlbumGroup(host: String, albumId: Int, groupId: String)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "album/" + albumId + "group/" + groupId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  //Request about Event
  //
  //  def createEvent(host: String, event: Event)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Post(host + "event", Event(event)))
  //  }
  //
  //  def getEvent(host: String, eventId: Int)(implicit system: ActorSystem): Future[Event] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[Event] = sendReceive ~> unmarshal[Event]
  //
  //    val response: Future[Event] = pipeline(Get(host + "event/" + eventId))
  //    response
  //  }
  //
  //  def deleteEvent(host: String, eventId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "event/" + eventId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //  def attendEvent(host: String, userId: String, eventId: Int)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Put(host + "event/" + eventpId))
  //  }
  //
  //  def cancleEvent(host: String, userId: String, eventId: Int)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "event/" + eventId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }
  //
  //   def addEventGroup(host: String, eventId: Int, groupId: String)(implicit system: ActorSystem): Future[HttpResponse] = {
  //    import system.dispatcher
  //
  //    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  //
  //    val response: Future[HttpResponse] = pipeline(Put(host + "event/" + eventId + "group/" + groupId))
  //  }
  //
  //  def removeEventGroup(host: String, eventId: Int, groupId: String)(implicit system: ActorSystem): Future[String] = {
  //    import system.dispatcher
  //    for {
  //      response <- (IO(Http) ? Delete(host + "event/" + eventId + "group/" + groupId)).mapTo[HttpResponse]
  //    } yield {
  //      response.status.toString
  //    }
  //  }

}
