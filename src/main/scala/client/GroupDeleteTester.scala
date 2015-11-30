package client

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import client.Main.{DeleteGroup}
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class GroupDeleteTester extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  implicit val timeout: Timeout = 60.seconds

  import spray.httpx.RequestBuilding._

  val system = context.system

  def deleteGroup(id: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Delete(host + "group/" + id))
    response

  }

  def receive = {
    case DeleteGroup(id: String) =>
      import system.dispatcher
      deleteGroup(id) onComplete {
        case Success(response) => log.info("Delete group {}, received response: {}", id, response)
        case Failure(error) => log.warning("Delete group {} request error: {}", id, error.getMessage)
      }
  }

}
