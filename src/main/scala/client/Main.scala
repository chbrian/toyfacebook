package client


import akka.actor.ActorSystem
import akka.event.Logging

import scala.util.{Failure, Success}

/**
  * Client App for benchmarking.
  * Created by alan on 11/24/2015.
  * Modified by xiaohui on 11/28/2015.
  */
object Main extends App with Requests {

  // Create an ActorSystem to host our client application in
  implicit val system = ActorSystem("client")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())

  import system.dispatcher

  val log = Logging(system, getClass)

  val host = "http://localhost:8080/"

  val id_1 = "wang"
  
  val id_2 = "wangyi"
  
  val id_3 = "yuan"
  
  val id_4 = "xiaoyong"
  
  val id_5 = "huang"
  
  val id_6 = "xiaohui"
  
  val postId = 1;

  val profileId = 1;
  
  val albumId = 1;
  
  val eventId = 1;
  
  val eventId2 = 0;
  
  // !!! All results are futures, they may return in random order! Careful
  
  //Test About User
  val result0 = createUser(host, id_1, id_2, "123")
  result0 onComplete {
    case Success(response) => log.info("Create user {}, received response: {}", id_1, response.status)
    case Failure(error) => log.warning("Create user {} request error: {}", id_1, error.getMessage)
  }

  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result1 = getUser(host, id_1)
  result1 onComplete {
    case Success(response) => log.info("Get user {}, received response: {}", id_1, response)
    case Failure(error) => log.warning("Get user {} request error: {}", id_1, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result2 = createUser(host, id_3, id_4, "gator")
  result2 onComplete {
    case Success(response) => log.info("Create user {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Create user {} request error: {}", id_3, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result3 = createUser(host, id_5, id_6, "happy")
  result3 onComplete {
    case Success(response) => log.info("Create user {}, received response: {}", id_5, response.status)
    case Failure(error) => log.warning("Create user {} request error: {}", id_5, error.getMessage)
  }

  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result4 = deleteUser(host, id_5)
  result4 onComplete {
    case Success(response) => log.info("Delete user {}, received response: {}", id_5, response)
    case Failure(error) => log.warning("Delete user {} request error: {}", id_5, error.getMessage)
  }
  
  //Test about Post
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result5 = createPost(host, id_3, "hello, world")
  result5 onComplete {
    case Success(response) => log.info("Add a Post {}, received response: {}", "hello, world", response.status)
    case Failure(error) => log.warning("Add a Post {} request error: {}", "hello, world", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result6 = createPost(host, id_1, "today is holiday")
  result6 onComplete {
    case Success(response) => log.info("Add a Post {}, received response: {}", "today is holiday", response.status)
    case Failure(error) => log.warning("Add a Post {} request error: {}", "today is holiday", error.getMessage)
  }
    
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result7 = getPost(host, postId)
  result7 onComplete {
    case Success(response) => log.info("Get A Post {}, received response: {}", postId, response)
    case Failure(error) => log.warning("Get A Post {} request error: {}", postId, error.getMessage)
  }
     
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result8 = removePost(host, postId)
  result8 onComplete {
    case Success(response) => log.info("Delete A Post {}, received response: {}", postId, response)
    case Failure(error) => log.warning("Delete A Post {} request error: {}", postId, error.getMessage)
  }
  
  //Test about Friend
   Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result9 = addFriend(host, id_1, id_3)
  result9 onComplete {
    case Success(response) => log.info("Add friend {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Add friend {} request error: {}", id_3, error.getMessage)
  }
   
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result10 = addFriend(host, id_3, id_1)
  result10 onComplete {
    case Success(response) => log.info("Add friend {}, received response: {}", id_1, response.status)
    case Failure(error) => log.warning("Add friend {} request error: {}", id_1, error.getMessage)
  }
  
   Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result11 = removeFriend(host, id_3, id_1)
  result11 onComplete {
    case Success(response) => log.info("Delete friend {}, received response: {}", id_1, response.status)
    case Failure(error) => log.warning("Delete friend {} request error: {}", id_1, error.getMessage)
  }
     
   //Test about Profile
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result12 = createProfile(host, "user", id_3)
  result12 onComplete {
    case Success(response) => log.info("Create a Profile {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Create a Profile {} request error: {}", id_3, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result13 = createProfile(host, "group", "dos")
  result13 onComplete {
    case Success(response) => log.info("Create a Profile {}, received response: {}", "dos", response.status)
    case Failure(error) => log.warning("Create a Profile {} request error: {}", "dos", error.getMessage)
  }
    
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result14 = getProfile(host, profileId)
  result14 onComplete {
    case Success(response) => log.info("Get Profile {}, received response: {}", profileId, response)
    case Failure(error) => log.warning("Get Profile {} request error: {}", profileId, error.getMessage)
  }
     
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result15 = deleteProfile(host, profileId)
  result15 onComplete {
    case Success(response) => log.info("Delete Profile {}, received response: {}", profileId, response)
    case Failure(error) => log.warning("Delete Profile {} request error: {}", profileId, error.getMessage)
  }

  //Test about Picture
  
  //Test about Album
   Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result16 = createAlbum(host, id_3, "holiday")
  result16 onComplete {
    case Success(response) => log.info("Create Album {}, received response: {}", "holiday", response.status)
    case Failure(error) => log.warning("Create Album {} request error: {}", "holiday", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result17 = createAlbum(host, id_1, "tomorrow")
  result17 onComplete {
    case Success(response) => log.info("Create Album {}, received response: {}", "tomorrow", response.status)
    case Failure(error) => log.warning("Create Album {} request error: {}", "tomorrow", error.getMessage)
  }
    
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result18 = getAlbum(host, albumId)
  result18 onComplete {
    case Success(response) => log.info("Get Profile {}, received response: {}", albumId, response)
    case Failure(error) => log.warning("Get Profile {} request error: {}", albumId, error.getMessage)
  }
     
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result19 = removeAlbum(host, id_1, albumId)
  result19 onComplete {
    case Success(response) => log.info("Delete Album {}, received response: {}", albumId, response)
    case Failure(error) => log.warning("Delete Album {} request error: {}", albumId, error.getMessage)
  }
  
  //Test about Group
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result20 = createGroup(host, "dos", id_3, "dosGroup")
  result20 onComplete {
    case Success(response) => log.info("Create Group {}, received response: {}", "dosGroup", response.status)
    case Failure(error) => log.warning("Create Group {} request error: {}", "dosGroup", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result21 = createGroup(host, "black", id_1, "blackGroup")
  result21 onComplete {
    case Success(response) => log.info("Create Group {}, received response: {}", "blackGroup", response.status)
    case Failure(error) => log.warning("Create Group {} request error: {}", "blackGroup", error.getMessage)
  }
    
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result22 = getGroup(host, "dos")
  result22 onComplete {
    case Success(response) => log.info("Get Group {}, received response: {}", "dos", response)
    case Failure(error) => log.warning("Get Group {} request error: {}", "dos", error.getMessage)
  }
     
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result23 = deleteGroup(host, "dos")
  result23 onComplete {
    case Success(response) => log.info("Delete Group {}, received response: {}", "dos", response)
    case Failure(error) => log.warning("Delete Group {} request error: {}", "dos", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result24 = joinUserGroup(host, id_3, "black")
  result24 onComplete {
    case Success(response) => log.info("Join Group {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Join Group {} request error: {}", id_3, error.getMessage)
  }
   
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result25 = leaveUserGroup(host, id_3, "black")
  result25 onComplete {
    case Success(response) => log.info("Leave Group {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Leave Group {} request error: {}", id_3, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result26 = addAlbumGroup(host, albumId, "black")
  result26 onComplete {
    case Success(response) => log.info("Add album to group {}, received response: {}", albumId, response.status)
    case Failure(error) => log.warning("Add album to group {} request error: {}", albumId, error.getMessage)
  }
   
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result27 = removeAlbumGroup(host, albumId, "black")
  result27 onComplete {
    case Success(response) => log.info("Remove Album from group{}, received response: {}", albumId, response.status)
    case Failure(error) => log.warning("Remove Album from group{} request error: {}", albumId, error.getMessage)
  }
  
  //Test about Event
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result28 = createEvent(host, id_3, "buybuybuy", "BlackFriday")
  result28 onComplete {
    case Success(response) => log.info("Create Event {}, received response: {}", "buybuybuy", response.status)
    case Failure(error) => log.warning("Create Event {} request error: {}", "buybuybuy", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result29 = createEvent(host, id_1, "football", "Sunday")
  result29 onComplete {
    case Success(response) => log.info("Create Event {}, received response: {}", "Sunday", response.status)
    case Failure(error) => log.warning("Create Event {} request error: {}", "Sunday", error.getMessage)
  }
    
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result30 = getEvent(host, eventId)
  result30 onComplete {
    case Success(response) => log.info("Get Event {}, received response: {}", eventId, response)
    case Failure(error) => log.warning("Get Event {} request error: {}", eventId, error.getMessage)
  }
     
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result31 = deleteEvent(host, eventId)
  result31 onComplete {
    case Success(response) => log.info("Delete Event {}, received response: {}", eventId, response)
    case Failure(error) => log.warning("Delete Event {} request error: {}", eventId, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result32 = attendEvent(host, id_3, eventId)
  result32 onComplete {
    case Success(response) => log.info("Attend Event {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Attend Event {} request error: {}", id_3, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result33 = cancleEvent(host, id_3, eventId)
  result33 onComplete {
    case Success(response) => log.info("Cancle Event {}, received response: {}", id_3, response.status)
    case Failure(error) => log.warning("Cancle Event {} request error: {}", id_3, error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result34 = addEventGroup(host, eventId2, "black")
  result34 onComplete {
    case Success(response) => log.info("Add Event to Group {}, received response: {}", "black", response.status)
    case Failure(error) => log.warning("Add Event to Group {} request error: {}", "black", error.getMessage)
  }
  
  Thread.sleep(1000)
  // TODO: use actor or future composition instead of sleep (blocking!)
  val result35 = removeEventGroup(host, eventId2, "black")
  result35 onComplete {
    case Success(response) => log.info("Remove Event to Group {}, received response: {}", "black", response.status)
    case Failure(error) => log.warning("Remove Event {} request error: {}", "black", error.getMessage)
  }

  result35 onComplete { _ => system.shutdown() 
}
}
