package client

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import facebook.Structures._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Requests made by the client
  * Created by alan on 11/24/2015.
  */
trait Requests {
  private implicit val timeout: Timeout = 10.seconds

  import spray.httpx.RequestBuilding._

  def createUser(host: String, id: String, name: String, password: String)(implicit system: ActorSystem): Future[HttpResponse] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

    val response: Future[HttpResponse] = pipeline(Post(host + "user", User(id, name, password)))
    response
  }

  def getUser(host: String, id: String)(implicit system: ActorSystem): Future[User] = {
    import system.dispatcher

    val pipeline: HttpRequest => Future[User] = sendReceive ~> unmarshal[User]

    val response: Future[User] = pipeline(Get(host + "info/" + id))
    response
  }

  def deleteUser(host: String, id: String)(implicit system: ActorSystem): Future[String] = {
    import system.dispatcher
    for {
      response <- (IO(Http) ? Delete(host + "user/" + id)).mapTo[HttpResponse]
    } yield {
      response.status.toString
    }
  }


}
