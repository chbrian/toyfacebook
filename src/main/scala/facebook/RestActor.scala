package facebook

/**
  * Created by xiaoyong on 11/25/2015.
  */

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, RequestContext, Route}
import scala.concurrent.Await
import scala.concurrent.duration._


class RestActor extends Actor with RestApi {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  implicit def actorRefFactory = context

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging {
  actor: Actor =>

  import Structures._

  implicit val timeout = Timeout(2.seconds)

  val userActor = context.actorSelection("/user/userActor")
  val postActor = context.actorSelection("/user/postActor")
  val albumActor = context.actorSelection("/user/albumActor")
  val pictureActor = context.actorSelection("/user/pictureActor")

  def routes: Route =
  //  User Actions
    pathPrefix("user") {
      pathEnd {
        post {
          entity(as[User]) { user => requestContext =>
            log.info("Get user creation request: {}", user)
            val responder = createResponder(requestContext)
            val future = userActor ? CreateUser(user)
            Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
              case true => responder ! "User created."
              case false => responder ! UserOpFailed
            }
          }
        }
      } ~
        path(Segment) { id =>
          get { requestContext =>
            log.info("Get user request: {}", id)
            val responder = createResponder(requestContext)
            val future = userActor ? GetUser(id)
            Await.result(future, timeout.duration).asInstanceOf[Option[User]] match {
              case Some(user: User) => responder ! user
              case None => responder ! UserOpFailed
            }
          } ~
            delete { requestContext =>
              log.info("Delete user request: {}", id)
              val responder = createResponder(requestContext)
              val future = userActor ? DeleteUser(id)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "User Deleted."
                case _ => responder ! UserOpFailed
              }
            }
        } ~
        pathPrefix("addFriend") {
          path(Segment / Segment) { (id, friendId) =>
            put { requestContext =>
              val responder = createResponder(requestContext)
              val future = userActor ? AddFriend(id, friendId)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Friend Added."
                case _ => responder ! UserOpFailed
              }
            }
          }
        } ~
        pathPrefix("removeFriend") {
          path(Segment / Segment) { (id, friendId) =>
            delete { requestContext => // delete method may be wrong.
              val responder = createResponder(requestContext)
              val future = userActor ? RemoveFriend(id, friendId)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Friend Removed."
                case _ => responder ! UserOpFailed
              }
            }
          }
        }
    } ~
      // Post Actions
      pathPrefix("post") {
        pathEnd {
          post {
            entity(as[Post]) { newPost => requestContext =>
              log.info("Get post creation request: {}", newPost)
              val responder = createResponder(requestContext)
              val future = postActor ? CreatePost(newPost)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Post created."
                case false => responder ! PostOpFailed
              }
            }
          }
        } ~
          path(Segment) { postId =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              val future = postActor ? GetPost(postId.toInt)
              Await.result(future, timeout.duration).asInstanceOf[Option[Post]] match {
                case Some(post: Post) => responder ! post
                case None => responder ! PostOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = postActor ? DeletePost(postId.toInt)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Post Deleted."
                  case false => responder ! PostOpFailed
                }
              }
          }
      } ~
      pathPrefix("album") {
        pathEnd {
          post {
            entity(as[Album]) { album => requestContext =>
              log.info("Get album creation request: {}", album)
              val responder = createResponder(requestContext)
              val future = albumActor ? CreateAlbum(album)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Album created."
                case false => responder ! AlbumOpFailed
              }
            }
          }
        } ~
          path(Segment) { albumId =>
            get { requestContext =>
              log.info("Get album get request: {}", albumId)
              val responder = createResponder(requestContext)
              val future = albumActor ? GetAlbum(albumId.toInt)
              Await.result(future, timeout.duration).asInstanceOf[Option[Album]] match {
                case Some(album: Album) => responder ! album
                case None => responder ! AlbumOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = albumActor ? DeleteAlbum(albumId.toInt)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Album Deleted."
                  case false => responder ! AlbumOpFailed
                }
              }
          }
      } ~
      pathPrefix("picture") {
        pathEnd {
          post {
            entity(as[Picture]) { picture => requestContext =>
              log.info("Get picture creation request: {}", picture)
              val responder = createResponder(requestContext)
              val future = pictureActor ? CreatePicture(picture)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Picture created."
                case false => responder ! PictureOpFailed
              }
            }
          }
        } ~
          path(Segment) { pictureId =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              val future = pictureActor ? GetPicture(pictureId.toInt)
              Await.result(future, timeout.duration).asInstanceOf[Option[Picture]] match {
                case Some(picture: Picture) => responder ! picture
                case None => responder ! PictureOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = pictureActor ? DeletePicture(pictureId.toInt)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Picture Deleted."
                  case false => responder ! PictureOpFailed
                }
              }
          }
      }

  //          } ~
  //      pathPrefix("addfriend") {
  //        path(Segment / Segment) { (id, friendId) =>
  //          put { requestContext =>
  //            val responder = createResponder(requestContext)
  //            USERS.addFriend(id, friendId) match {
  //              case true => responder ! FriendAdded
  //              case _ => responder ! UserOpFailed
  //            }
  //          }
  //        }
  //      } ~
  //      pathPrefix("info") {
  //        path(Segment) { id =>
  //          get { requestContext =>
  //            val responder = createResponder(requestContext)
  //            USERS.getUserInfo(id) match {
  //              case Some(info: UserInfo) => responder ! info
  //              case None => responder ! UserOpFailed
  //            }
  //          }
  //        }
  //      } ~

  //  // Picture Actions
  //  pathPrefix("picture") {
  //    pathEnd {
  //      post {
  //        entity(as[Picture]) { newPicture => mapRequestContext =>
  //          val responder = createResponder(requestContext)
  //
  //        }
  //      }
  //    }
  //  }


  private def createResponder(requestContext: RequestContext): ActorRef = {
    context.actorOf(Props(new Responder(requestContext)))
  }

}