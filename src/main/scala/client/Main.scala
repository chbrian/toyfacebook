package client

import akka.actor.ActorSystem
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

  // Create an ActorSystem to host our client application in
  implicit val system = ActorSystem("client")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())

  implicit val timeout: Timeout = 60.seconds

  implicit val executionContext: ExecutionContext = ExecutionContext.global


  val userTestScale = 10
  val friendTestScale = 10 // friendTestScale should <= userTestScale
  val groupTestScale = 100
  val postTestScale = 100
  val albumTestScale = 100
  val pictureTestScale = 100
  val profileTestScale = 10 // profileTestScale should be smaller than user, group and event
  val eventTestScale = 100

  // compose test cases
  system.scheduler.scheduleOnce(0.seconds)(Tests.createUser)

  system.scheduler.scheduleOnce(2.seconds)(Tests.addFriend)

  system.scheduler.scheduleOnce(3.seconds)(Tests.getUser)

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
