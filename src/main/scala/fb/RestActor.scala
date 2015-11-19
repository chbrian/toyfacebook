package fb

import akka.actor.{Actor, ActorRef, Props, ActorLogging}
import akka.util.Timeout
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, RequestContext, Route}
import scala.concurrent.duration._

/**
  * Akka actor for handling REST request and routing.
  * Created by alan on 11/17/2015.
  */
class RestActor extends Actor with RestApi {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  implicit def actorRefFactory = context

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging {
  actor: Actor =>

  import Structures._

  implicit val timeout = Timeout(10.seconds)

  val USERS = new UserMap()

  def routes: Route =
    pathPrefix("user") {
      pathEnd {
        post {
          entity(as[User]) { user => requestContext =>
            val responder = createResponder(requestContext)
            USERS.createUser(user) match {
              case true => responder ! UserCreated
              case _ => responder ! UserOpFailed
            }
          }
        }
      } ~
      path(Segment) { id =>
        get { requestContext =>
          val responder = createResponder(requestContext)
          USERS.getUser(id) match {
            case Some(username: String) => responder ! username
            case None => responder ! UserOpFailed
          }
        } ~
        delete { requestContext =>
          val responder = createResponder(requestContext)
          USERS.deleteUser(id) match {
            case true => responder ! UserDeleted
            case _ => responder ! UserOpFailed
          }
        }
      }
    } ~
    pathPrefix("addfriend") {
      path(Segment / Segment) { (id, friendId) =>
        put { requestContext =>
          val responder = createResponder(requestContext)
          USERS.addFriend(id, friendId) match {
            case true => responder ! FriendAdded
            case _ => responder ! UserOpFailed
          }
        }
      }
    } ~
    pathPrefix("getfriends") {
      path(Segment) { id =>
        get { requestContext =>
          val responder = createResponder(requestContext)
          USERS.getFriends(id) match {
            case Some(friendsSeq: Seq[String]) => responder ! friendsSeq
            case None => responder ! UserOpFailed
          }
        }
      }
    }

  private def createResponder(requestContext: RequestContext): ActorRef = {
    context.actorOf(Props(new Responder(requestContext)))
  }

}