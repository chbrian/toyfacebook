package client

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Client App for benchmarking.
  * Created by alan on 11/24/2015.
  * Modified by xiaohui on 11/28/2015.
  * Modified by xiaoyong on 11/29/2015.
  */
object Main extends App with Requests {

  case class CreateUser(id: String)

  case class GetUser(id: String)

  case class DeleteUser(id: String)

  case class AddFriend(id1: String, id2: String)

  case class RemoveFriend(id1: String, id2: String)

  case class CreateGroup(id: String)

  case class GetGroup(id: String)

  case class DeleteGroup(id: String)

  case class CreatePost(userId: String)

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

  val scala = 3
  val alphabetList = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList

  val idList = alphabetList.combinations(scala).toList


  // user creation test
  def createUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (userTester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      userTester ! CreateUser(sb.toString)
      index += 1
    }
  }

  // user getter test
  def getUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (userTester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      userTester ! GetUser(sb.toString)
      index += 1
    }
  }

  // user delete test
  def deleteUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))

    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! DeleteUser(sb.toString)
      index += 1
    }

  }

  // user friend test
  def addFriend: Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (friendTester <- testerArray) {
      val sb1 = new StringBuilder
      idList(index).map(x => sb1.append(x))

      for (i <- 0 until friendTestScale) {
        val sb2 = new StringBuilder
        idList(i).map(x => sb2.append(x))
        friendTester ! AddFriend(sb1.toString, sb2.toString)
      }
      index += 1
    }
  }

  def removeFriend: Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (friendTester <- testerArray) {
      val sb1 = new StringBuilder
      idList(index).map(x => sb1.append(x))

      for (i <- 0 until friendTestScale) {
        val sb2 = new StringBuilder
        idList(i).map(x => sb2.append(x))
        friendTester ! RemoveFriend(sb1.toString, sb2.toString)
      }
      index += 1
    }
  }

  // group creation test
  def createGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateGroup(sb.toString)
      index += 1
    }
  }

  // group getter test
  def getGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! GetGroup(sb.toString)
      index += 1
    }
  }

  //group delete test
  def deleteGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! DeleteGroup(sb.toString)
      index += 1
    }
  }

  // post creation test, each user create two posts
  def createPost: Unit = {
    val testerArray = (1 to postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreatePost(sb.toString)
      tester ! CreatePost(sb.toString)
      index += 1
    }
  }

  // post getter test
  def getPost: Unit = {
    val testerArray = (1 to 2 * postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetPost(index)
      index += 1
    }
  }

  // post delete test
  def deletePost: Unit = {
    val testerArray = (1 to 2 * postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeletePost(index)
      index += 1
    }
  }

  // album creation test, each user create an album
  def createAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateAlbum(sb.toString)
      index += 1
    }
  }

  // album getter test
  def getAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetAlbum(index)
      index += 1
    }
  }

  // album delete test
  def deleteAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeleteAlbum(index)
      index += 1
    }
  }

  // picture creation test, each album create one picture, the picture is uploaded in the client side
  def createPicture: Unit = {
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    val location = "src\\main\\scala\\client\\test2.jpg"
    for (tester <- testerArray) {
      tester ! CreatePicture(index, location)
      index += 1
    }
  }

  // picture get test
  def getPicture: Unit = {
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0

    for (tester <- testerArray) {
      val newLocation = "src\\main\\scala\\client\\" + index + "received.jpg"
      tester ! GetPicture(index, newLocation)
      index += 1
    }
  }

  // picture delete test
  def deletePicture: Unit = {
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeletePicture(index)
      index += 1
    }
  }

  // event creation test
  def createEvent: Unit = {
    val testerArray = (1 to eventTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateEvent(sb.toString)
      index += 1
    }
  }

  // profile getter test
  def getEvent: Unit = {
    val testerArray = (1 to eventTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetEvent(index)
      index += 1
    }
  }

  // profile delete test
  def deleteEvent: Unit = {
    val testerArray = (1 to eventTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeleteEvent(index)
      index += 1
    }
  }

  // profile creation test
  def createProfile: Unit = {
    val testerArray = (1 to profileTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateProfile("user", sb.toString)
      tester ! CreateProfile("group", sb.toString)
      tester ! CreateProfile("event", sb.toString)
      index += 1
    }
  }

  // profile getter test
  def getProfile: Unit = {
    val testerArray = (1 to profileTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetProfile(index)
      index += 1
    }
  }

  // profile delete test
  def deleteProfile: Unit = {
    val testerArray = (1 to profileTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeleteProfile(index)
      index += 1
    }
  }

  val userTestScale = 2
  val friendTestScale = 1
  // friendTestScale should be <= userTestScale
  val groupTestScale = 1
  val postTestScale = 1
  val albumTestScale = 1
  val pictureTestScale = 1
  val profileTestScale = 1
  // profileTestScale should be smaller than user, group and event
  val eventTestScale = 1


  // compose test case
  createUser
  Thread sleep 1000
  addFriend
  Thread sleep 1000
  // removeFriend
  // Thread sleep 1000
  getUser
  Thread sleep 1000
  // deleteUser
  // Thread sleep 1000
  createGroup
  Thread sleep 1000
  getGroup
  Thread sleep 1000
  //  deleteGroup
  //  Thread sleep 1000
  //  createPost
  //  Thread sleep 1000
  //  getPost
  //  Thread sleep 1000
  //  deletePost
  //  Thread sleep 1000
  createAlbum
  Thread sleep 1000
  getAlbum
  Thread sleep 1000
  //  deleteAlbum
  //  Thread sleep 1000

  //  createPicture
  //  Thread sleep 1000
  //  getPicture
  //  Thread sleep 1000
  //  deletePicture
  //  Thread sleep 1000

  createEvent
  Thread sleep 1000
  getEvent
  Thread sleep 1000
  deleteEvent
  Thread sleep 1000

  createProfile
  Thread sleep 1000
  //  getProfile
  //  Thread sleep 1000
  deleteProfile
  Thread sleep 1000

  system.shutdown()
}
