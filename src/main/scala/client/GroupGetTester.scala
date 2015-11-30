package client

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import client.Main.GetGroup
import facebook.Structures._

import akka.actor.{Props, ActorLogging, ActorSystem, Actor}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._

class GroupGetTester extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  implicit val timeout: Timeout = 60.seconds

  import spray.httpx.RequestBuilding._

  val system = context.system

  def getGroup(id: String): Future[Group] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[Group] = sendReceive ~> unmarshal[Group]

    val response: Future[Group] = pipeline(Get(host + "group/" + id))
    response
  }

  def receive = {
    case GetGroup(id: String) =>
      import system.dispatcher
      getGroup(id) onComplete {
        case Success(response) => log.info("Get group {}, received response: {}", id, response)
        case Failure(error) => log.warning("Get group {} request error: {}", id, error.getMessage)
      }
  }

}
