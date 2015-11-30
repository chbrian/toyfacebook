package client


import facebook.Structures.User

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import client.Main._

import akka.actor.{Props, ActorLogging, ActorSystem, Actor}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport._

/**
  * Created by xiaoyong on 11/30/2015.
  */


class UserCreationTester extends Actor with ActorLogging {

  val host = "http://localhost:8080/"

  val alphabet = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')

  def randomString(length: Int) = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(alphabet(Random.nextInt(62)))
    }
    sb.toString
  }

  implicit val timeout: Timeout = 60.seconds

  val system = context.system

  def createUser(id: String, name: String, password: String): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "user", User(id, name, password)))
    response
  }





  def receive = {
    case CreateUser(id: String) =>
      import system.dispatcher
      createUser(id, randomString(Random.nextInt(10)), randomString(Random.nextInt(5))) onComplete {
        case Success(response) => log.info("Create user {}, received response: {}", id, response.status)
        case Failure(error) => log.warning("Create user {} request error: {}", id, error.getMessage)
      }
  }
}
