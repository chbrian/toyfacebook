package client

import akka.actor.Props

/**
  * Created by alan on 12/1/15.
  */
object Tests {
  import Main._

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


  val scala = 3
  val alphabetList = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList

  val idList = alphabetList.combinations(scala).toList

  // user creation test
  def createUser: Unit = {
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
  def getUser: Unit = {
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
  def deleteUser: Unit = {
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

  // Tread sleep
  def sleep = Thread sleep 1000
}
