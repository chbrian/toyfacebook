package facebook

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.duration._

object Server extends App {

  // Create an ActorSystem to host our application in
  implicit val system = ActorSystem("fb")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())

  // create and start REST api service actor
  val restActor = system.actorOf(Props[RestActor], "restActor")
  val userActor = system.actorOf(Props[UserActor], "userActor")
  val postActor = system.actorOf(Props[PostActor], "postActor")
  val albumActor = system.actorOf(Props[AlbumActor], "albumActor")
  val pictureActor = system.actorOf(Props[PictureActor], "pictureActor")
  val profileActor = system.actorOf(Props[ProfileActor], "profileActor")
  val groupActor = system.actorOf(Props[GroupActor], "groupActor")
  val eventActor = system.actorOf(Props[EventActor], "eventActor")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(30.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(restActor, interface = "localhost", port = 8080)
}
