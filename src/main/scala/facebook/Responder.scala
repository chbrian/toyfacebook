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
    case UserCreated =>
      requestContext.complete(StatusCodes.Created)
      killYourself

    case UserDeleted =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case UserOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    // Used for return id, name or any String value.
//    case string: String =>
//      requestContext.complete(StatusCodes.OK, string)
//      killYourself

    case user: User =>
      requestContext.complete(StatusCodes.OK, user)
      killYourself

//    case info: UserInfo =>
//      requestContext.complete(StatusCodes.OK, info)
//      killYourself

//    case FriendAdded =>
//      requestContext.complete(StatusCodes.OK)
//      killYourself
//
//    case FriendOpFailed =>
//      requestContext.complete(StatusCodes.NotFound)
//      killYourself
//
//    case post: Post =>
//      requestContext.complete(StatusCodes.OK, post)
//      killYourself
//
//    case PostDeleted =>
//      requestContext.complete(StatusCodes.OK)
//      killYourself
//
//    case PostOpFailed =>
//      requestContext.complete(StatusCodes.NotFound)
//      killYourself
  }

  private def killYourself = self ! PoisonPill
}
