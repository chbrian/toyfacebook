package client

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

/**
  * Client App for benchmarking.
  * Created by alan on 11/24/2015.
  * Modified by xiaohui on 11/28/2015.
  * Modified by xiaoyong on 11/29/2015.
  */
object Main extends App {

  case object CreateUser

  case class CreateUser(id: String)

  case class GetUser(id: String)

  case class DeleteUser(id: String)

  case class AddFriend(id: String)

  case class RemoveFriend(id1: String, id2: String)

  case class CreateGroup(id: String)

  case class GetGroup(id: String)

  case class DeleteGroup(id: String)

  case class CreatePost(content: String)

  case class GetPost(postId: Int)

  case class DeletePost(postId: Int)

  case class CreateAlbum(userId: String)

  case class GetAlbum(albumId: Int)

  case class DeleteAlbum(albumId: Int)

  case class CreatePicture(albumId: Int, location: String)

  case class GetPicture(pictureId: Int, location: String)

  case class DeletePicture(pictureId: Int)

  case class CreateProfile(fbType: String, fbId: String)

  case class GetProfile(profileId: Int)

  case class DeleteProfile(profileId: Int)

  case class CreateEvent(userId: String)

  case class GetEvent(eventId: Int)

  case class DeleteEvent(eventId: Int)

  // Create an ActorSystem to host our client application in
  implicit val system = ActorSystem("client")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())

  implicit val timeout: Timeout = 60.seconds

  implicit val executionContext: ExecutionContext = ExecutionContext.global


  val userTestScale = 10
  val friendTestScale = 10
  // friendTestScale should <= userTestScale
  val groupTestScale = 100
  val postTestScale = 100
  val albumTestScale = 100
  val pictureTestScale = 100
  val profileTestScale = 10
  // profileTestScale should be smaller than user, group and event
  val eventTestScale = 100

  val user1Name = "yxy"
  val user1 = system.actorOf(Props[UserClient], name = user1Name)
  user1 ! CreateUser(user1Name)

  val user2Name = "yuan"
  val user2 = system.actorOf(Props[UserClient], name = user2Name)
  user2 ! CreateUser(user2Name)


  user1 ! GetUser(user1Name)
  Thread.sleep(2000)
  user1 ! AddFriend(user2Name)
  user2 ! AddFriend(user1Name)

  Thread.sleep(2000)
  user1 ! GetUser(user1Name)
  user2 ! GetUser(user2Name)
  val content1 = "hello world"
  val content2 = "best regards"
  user1 ! CreatePost(content1)
  user2 ! CreatePost(content2)

  Thread.sleep(1000)
  user1 ! GetPost(1)
  user2 ! GetPost(1)
  // compose test cases

  //  system.scheduler.scheduleOnce(0.seconds)(Tests.createUser)
  //
  //  system.scheduler.scheduleOnce(2.seconds)(Tests.addFriend)
  //
  //  system.scheduler.scheduleOnce(3.seconds)(Tests.getUser)

  //  system.scheduler.scheduleOnce(4.seconds)(Tests.removeFriend)
  //
  //  system.scheduler.scheduleOnce(5.seconds)(Tests.createGroup)
  //
  //  system.scheduler.scheduleOnce(6.seconds)(Tests.getGroup)
  //
  //  system.scheduler.scheduleOnce(7.seconds)(Tests.createPost)
  //
  //  system.scheduler.scheduleOnce(8.seconds)(Tests.getPost)
  //
  //  system.scheduler.scheduleOnce(9.seconds)(Tests.deletePost)
  //
  //  system.scheduler.scheduleOnce(10.seconds)(Tests.createAlbum)
  //
  //  system.scheduler.scheduleOnce(11.seconds)(Tests.getAlbum)
  //
  //  system.scheduler.scheduleOnce(12.seconds)(Tests.deleteAlbum)
  //
  //  // system.scheduler.scheduleOnce(13.seconds)(Tests.createPicture)
  //
  //  // system.scheduler.scheduleOnce(14.seconds)(Tests.getPicture)
  //
  //  system.scheduler.scheduleOnce(15.seconds)(Tests.createEvent)
  //
  //  system.scheduler.scheduleOnce(16.seconds)(Tests.getEvent)
  //
  //  system.scheduler.scheduleOnce(17.seconds)(Tests.deleteEvent)
  //
  //  system.scheduler.scheduleOnce(18.seconds)(Tests.createProfile)
  //
  //  system.scheduler.scheduleOnce(19.seconds)(Tests.deleteProfile)
  //
  //  system.scheduler.scheduleOnce(20.seconds)(Tests.deleteUser)
  //
  //  system.scheduler.scheduleOnce(60.seconds)(system.shutdown())
}
