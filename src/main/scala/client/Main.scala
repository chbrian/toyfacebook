package client


import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.event.Logging
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success}
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

  case class CreateGroup(id: String)

  case class GetGroup(id: String)

  case class DeleteGroup(id: String)


  // Create an ActorSystem to host our client application in
  implicit val system = ActorSystem("client")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())
  implicit val timeout: Timeout = 60.seconds

  val scala = 3
  val alphabetList = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList

  val idList = alphabetList.combinations(scala).toList
  val aa = idList(5)

  val userTestScale = 10

  // user creation test
  val userCreationArray = (1 to userTestScale).map(x => system.actorOf(Props[UserCreationTester], "userCreationTester" + x.toString))
  var index = 0
  for (userCreator <- userCreationArray) {
    val sb = new StringBuilder
    idList(index).map(x => sb.append(x))
    userCreator ! CreateUser(sb.toString)
    index += 1
  }
  Thread sleep 1000

  // user getter test
  val userGetArray = (1 to userTestScale).map(x => system.actorOf(Props[UserGetTester], "userGetTester" + x.toString))
  index = 0
  for (userGetter <- userGetArray) {
    val sb = new StringBuilder
    idList(index).map(x => sb.append(x))
    userGetter ! GetUser(sb.toString)
    index += 1
  }
  Thread sleep 1000

  //user delete test

  //  val userDeleteArray = (1 to userTestScale).map(x => system.actorOf(Props[UserDeleteTester], "userDeleteTester" + x.toString))
  //
  //  Thread sleep 1000
  //  index = 0
  //  for (userDelete <- userDeleteArray) {
  //    val sb = new StringBuilder
  //    idList(index).map(x => sb.append(x))
  //    userDelete ! DeleteUser(sb.toString)
  //    index += 1
  //  }

  Thread sleep 1000

  // group creation test
  val groupTestScale = 10
  val groupCreationArray = (1 to groupTestScale).map(x => system.actorOf(Props[GroupCreationTester], "groupCreationTester" + x.toString))
  index = 0
  for (groupCreator <- groupCreationArray) {
    val sb = new StringBuilder
    idList(index).map(x => sb.append(x))
    groupCreator ! CreateGroup(sb.toString)
    index += 1
  }
  Thread sleep 1000

  // group getter test
  val groupGetArray = (1 to groupTestScale).map(x => system.actorOf(Props[GroupGetTester], "groupGetTester" + x.toString))
  index = 0
  for (groupGetter <- groupGetArray) {
    val sb = new StringBuilder
    idList(index).map(x => sb.append(x))
    groupGetter ! GetGroup(sb.toString)
    index += 1
  }
  Thread sleep 1000

  //group delete test
  val groupDeleteArray = (1 to groupTestScale).map(x => system.actorOf(Props[GroupDeleteTester], "groupDeleteTester" + x.toString))
  Thread sleep 1000
  index = 0
  for (groupDelete <- groupDeleteArray) {
    val sb = new StringBuilder
    idList(index).map(x => sb.append(x))
    groupDelete ! DeleteGroup(sb.toString)
    index += 1
  }


  //
  //  val groupTestScale = 10
  //  val groupTestArray = (0 to groupTestScale).map(x => system.actorOf(Props[UserCreationTester], "userTestActor" + x))
  //  for (groupTest <- groupTestArray)
  //    groupTest ! Test

  //  (0 to userTestScale).map(x => system.actorOf(Props[UserTest], "userTestActor" + x))
  //  (0 to userTestScale).map(x => system.actorSelection("userTestActor" + x) ! Test)

  //  import system.dispatcher
  //
  //  val log = Logging(system, getClass)
  //
  //  // !!! All results are futures, they may return in random order! Careful
  //
  //  //Test About User
  //  val result0 = createUser(host, id_1, name_1, "123")
  //  result0 onComplete {
  //    case Success(response) => log.info("Create user {}, received response: {}", id_1, response.status)
  //    case Failure(error) => log.warning("Create user {} request error: {}", id_1, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result1 = getUser(host, id_1)
  //  result1 onComplete {
  //    case Success(response) => log.info("Get user {}, received response: {}", id_1, response)
  //    case Failure(error) => log.warning("Get user {} request error: {}", id_1, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result2 = createUser(host, id_3, id_4, "gator")
  //  result2 onComplete {
  //    case Success(response) => log.info("Create user {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Create user {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result3 = createUser(host, id_5, id_6, "happy")
  //  result3 onComplete {
  //    case Success(response) => log.info("Create user {}, received response: {}", id_5, response.status)
  //    case Failure(error) => log.warning("Create user {} request error: {}", id_5, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result4 = deleteUser(host, id_5)
  //  result4 onComplete {
  //    case Success(response) => log.info("Delete user {}, received response: {}", id_5, response)
  //    case Failure(error) => log.warning("Delete user {} request error: {}", id_5, error.getMessage)
  //  }
  //
  //  //Test about Post
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result5 = createPost(host, id_3, "hello, world")
  //  result5 onComplete {
  //    case Success(response) => log.info("Add a Post {}, received response: {}", "hello, world", response.status)
  //    case Failure(error) => log.warning("Add a Post {} request error: {}", "hello, world", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result6 = createPost(host, id_1, "today is holiday")
  //  result6 onComplete {
  //    case Success(response) => log.info("Add a Post {}, received response: {}", "today is holiday", response.status)
  //    case Failure(error) => log.warning("Add a Post {} request error: {}", "today is holiday", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result7 = getPost(host, postId)
  //  result7 onComplete {
  //    case Success(response) => log.info("Get A Post {}, received response: {}", postId, response)
  //    case Failure(error) => log.warning("Get A Post {} request error: {}", postId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result8 = removePost(host, postId.toInt)
  //  result8 onComplete {
  //    case Success(response) => log.info("Delete A Post {}, received response: {}", postId, response)
  //    case Failure(error) => log.warning("Delete A Post {} request error: {}", postId, error.getMessage)
  //  }
  //
  //  //Test about Friend
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result9 = addFriend(host, id_1, id_3)
  //  result9 onComplete {
  //    case Success(response) => log.info("Add friend {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Add friend {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result10 = addFriend(host, id_3, id_1)
  //  result10 onComplete {
  //    case Success(response) => log.info("Add friend {}, received response: {}", id_1, response.status)
  //    case Failure(error) => log.warning("Add friend {} request error: {}", id_1, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result11 = removeFriend(host, id_3, id_1)
  //  result11 onComplete {
  //    case Success(response) => log.info("Delete friend {}, received response: {}", id_1, response.status)
  //    case Failure(error) => log.warning("Delete friend {} request error: {}", id_1, error.getMessage)
  //  }
  //
  //  //Test about Profile
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result12 = createProfile(host, "user", id_3)
  //  result12 onComplete {
  //    case Success(response) => log.info("Create a Profile {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Create a Profile {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result13 = createProfile(host, "group", "dos")
  //  result13 onComplete {
  //    case Success(response) => log.info("Create a Profile {}, received response: {}", "dos", response.status)
  //    case Failure(error) => log.warning("Create a Profile {} request error: {}", "dos", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result14 = getProfile(host, profileId)
  //  result14 onComplete {
  //    case Success(response) => log.info("Get Profile {}, received response: {}", profileId, response)
  //    case Failure(error) => log.warning("Get Profile {} request error: {}", profileId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result15 = deleteProfile(host, profileId)
  //  result15 onComplete {
  //    case Success(response) => log.info("Delete Profile {}, received response: {}", profileId, response)
  //    case Failure(error) => log.warning("Delete Profile {} request error: {}", profileId, error.getMessage)
  //  }
  //
  //  //Test about Picture
  //
  //  //Test about Album
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result16 = createAlbum(host, id_3, "holiday")
  //  result16 onComplete {
  //    case Success(response) => log.info("Create Album {}, received response: {}", "holiday", response.status)
  //    case Failure(error) => log.warning("Create Album {} request error: {}", "holiday", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result17 = createAlbum(host, id_1, "tomorrow")
  //  result17 onComplete {
  //    case Success(response) => log.info("Create Album {}, received response: {}", "tomorrow", response.status)
  //    case Failure(error) => log.warning("Create Album {} request error: {}", "tomorrow", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result18 = getAlbum(host, albumId)
  //  result18 onComplete {
  //    case Success(response) => log.info("Get Profile {}, received response: {}", albumId, response)
  //    case Failure(error) => log.warning("Get Profile {} request error: {}", albumId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result19 = removeAlbum(host, id_1, albumId)
  //  result19 onComplete {
  //    case Success(response) => log.info("Delete Album {}, received response: {}", albumId, response)
  //    case Failure(error) => log.warning("Delete Album {} request error: {}", albumId, error.getMessage)
  //  }
  //
  //  //Test about Group
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result20 = createGroup(host, "dos", id_3, "dosGroup")
  //  result20 onComplete {
  //    case Success(response) => log.info("Create Group {}, received response: {}", "dosGroup", response.status)
  //    case Failure(error) => log.warning("Create Group {} request error: {}", "dosGroup", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result21 = createGroup(host, "black", id_1, "blackGroup")
  //  result21 onComplete {
  //    case Success(response) => log.info("Create Group {}, received response: {}", "blackGroup", response.status)
  //    case Failure(error) => log.warning("Create Group {} request error: {}", "blackGroup", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result22 = getGroup(host, "dos")
  //  result22 onComplete {
  //    case Success(response) => log.info("Get Group {}, received response: {}", "dos", response)
  //    case Failure(error) => log.warning("Get Group {} request error: {}", "dos", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result23 = deleteGroup(host, "dos")
  //  result23 onComplete {
  //    case Success(response) => log.info("Delete Group {}, received response: {}", "dos", response)
  //    case Failure(error) => log.warning("Delete Group {} request error: {}", "dos", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result24 = joinUserGroup(host, id_3, "black")
  //  result24 onComplete {
  //    case Success(response) => log.info("Join Group {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Join Group {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result25 = leaveUserGroup(host, id_3, "black")
  //  result25 onComplete {
  //    case Success(response) => log.info("Leave Group {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Leave Group {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result26 = addAlbumGroup(host, albumId, "black")
  //  result26 onComplete {
  //    case Success(response) => log.info("Add album to group {}, received response: {}", albumId, response.status)
  //    case Failure(error) => log.warning("Add album to group {} request error: {}", albumId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result27 = removeAlbumGroup(host, albumId, "black")
  //  result27 onComplete {
  //    case Success(response) => log.info("Remove Album from group{}, received response: {}", albumId, response.status)
  //    case Failure(error) => log.warning("Remove Album from group{} request error: {}", albumId, error.getMessage)
  //  }
  //
  //  //Test about Event
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result28 = createEvent(host, id_3, "buybuybuy", "BlackFriday")
  //  result28 onComplete {
  //    case Success(response) => log.info("Create Event {}, received response: {}", "buybuybuy", response.status)
  //    case Failure(error) => log.warning("Create Event {} request error: {}", "buybuybuy", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result29 = createEvent(host, id_1, "football", "Sunday")
  //  result29 onComplete {
  //    case Success(response) => log.info("Create Event {}, received response: {}", "Sunday", response.status)
  //    case Failure(error) => log.warning("Create Event {} request error: {}", "Sunday", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result30 = getEvent(host, eventId)
  //  result30 onComplete {
  //    case Success(response) => log.info("Get Event {}, received response: {}", eventId, response)
  //    case Failure(error) => log.warning("Get Event {} request error: {}", eventId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result31 = deleteEvent(host, eventId)
  //  result31 onComplete {
  //    case Success(response) => log.info("Delete Event {}, received response: {}", eventId, response)
  //    case Failure(error) => log.warning("Delete Event {} request error: {}", eventId, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result32 = attendEvent(host, id_3, eventId)
  //  result32 onComplete {
  //    case Success(response) => log.info("Attend Event {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Attend Event {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result33 = cancleEvent(host, id_3, eventId)
  //  result33 onComplete {
  //    case Success(response) => log.info("Cancle Event {}, received response: {}", id_3, response.status)
  //    case Failure(error) => log.warning("Cancle Event {} request error: {}", id_3, error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result34 = addEventGroup(host, eventId2, "black")
  //  result34 onComplete {
  //    case Success(response) => log.info("Add Event to Group {}, received response: {}", "black", response.status)
  //    case Failure(error) => log.warning("Add Event to Group {} request error: {}", "black", error.getMessage)
  //  }
  //
  //  Thread.sleep(1000)
  //  // TODO: use actor or future composition instead of sleep (blocking!)
  //  val result35 = removeEventGroup(host, eventId2, "black")
  //  result35 onComplete {
  //    case Success(response) => log.info("Remove Event to Group {}, received response: {}", "black", response.status)
  //    case Failure(error) => log.warning("Remove Event {} request error: {}", "black", error.getMessage)
  //  }
  //
  //  result35 onComplete { _ => system.shutdown()
  //  }
}
