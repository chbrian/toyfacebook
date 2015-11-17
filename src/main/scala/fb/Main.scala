package fb

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("fb")

  // create and start rest api service actor
  val rest_api = system.actorOf(Props[MyServiceActor], "rest-api")

  implicit val timeout = Timeout(10.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(rest_api, interface = "localhost", port = 8080)
}
