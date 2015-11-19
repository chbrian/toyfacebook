package fb

import akka.actor.{PoisonPill, ActorLogging, Actor}
import spray.http.StatusCodes
import spray.routing.RequestContext

/**
  * Akka Actor for sending response to a client.
  * Created by alan on 11/17/2015.
  */
class Responder(requestContext: RequestContext) extends Actor with ActorLogging {

  import Structures._

  def receive = {
    case UserCreated =>
      requestContext.complete(StatusCodes.Created)
      killYourself

    case UserDeleted =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case UserOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case FriendAdded =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case FriendOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself
  }

  private def killYourself = self ! PoisonPill
}
