package facebook

import akka.actor.{Actor, ActorLogging, PoisonPill}
import spray.http.StatusCodes
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

/**
  * Akka Actor for sending response to a client.
  * Created by alan on 11/17/2015.
  */
class Responder(requestContext: RequestContext) extends Actor with ActorLogging {

  import Structures._

  def receive = {

    case UserOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case user: User =>
      requestContext.complete(StatusCodes.OK, user)
      killYourself

    case post: Post =>
      requestContext.complete(StatusCodes.OK, post)
      killYourself

    case PostOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case str: String =>
      requestContext.complete(StatusCodes.OK, str)

  }

  private def killYourself = self ! PoisonPill
}
