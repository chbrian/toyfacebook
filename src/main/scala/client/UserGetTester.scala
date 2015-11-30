package client

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import client.Main.GetUser
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

class UserGetTester extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  implicit val timeout: Timeout = 60.seconds


  val system = context.system

  def getUser(id: String): Future[User] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[User] = sendReceive ~> unmarshal[User]

    val response: Future[User] = pipeline(Get(host + "user/" + id))
    response
  }

  def receive = {
    case GetUser(id: String) =>
      import system.dispatcher
      getUser(id) onComplete {
        case Success(response) => log.info("Get user {}, received response: {}", id, response)
        case Failure(error) => log.warning("Get user {} request error: {}", id, error.getMessage)
      }
  }

}
