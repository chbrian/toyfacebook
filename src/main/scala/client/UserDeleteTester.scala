package client

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import client.Main.DeleteUser

import akka.actor.{ActorLogging, Actor}
import akka.io.IO
import akka.util.Timeout

import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._
import spray.httpx.RequestBuilding._

class UserDeleteTester extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  implicit val timeout: Timeout = 60.seconds

  import spray.httpx.RequestBuilding._

  val system = context.system

  def deleteUser(id: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Delete(host + "user/" + id))
    response

  }

  def receive = {
    case DeleteUser(id: String) =>
      import system.dispatcher
      deleteUser(id) onComplete {
        case Success(response) => log.info("Delete user {}, received response: {}", id, response)
        case Failure(error) => log.warning("Delete user {} request error: {}", id, error.getMessage)
      }
  }

}
