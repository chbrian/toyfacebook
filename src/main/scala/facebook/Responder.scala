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

    case album: Album =>
      requestContext.complete(StatusCodes.OK, album)
      killYourself

    case AlbumOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case picture: Picture =>
      requestContext.complete(StatusCodes.OK, picture)
      killYourself

    case PictureOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case profile: Profile =>
      requestContext.complete(StatusCodes.OK, profile)
      killYourself

    case ProfileOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case group: Group =>
      requestContext.complete(StatusCodes.OK, group)
      killYourself

    case GroupOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case event: Event =>
      requestContext.complete(StatusCodes.OK, event)
      killYourself

    case EventOpFailed =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case msg: String =>
      requestContext.complete(StatusCodes.OK, msg)
      killYourself
  }

  private def killYourself = self ! PoisonPill
}
