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
  val profileActor = context.actorSelection("/user/profileActor")
  val groupActor = context.actorSelection("/user/groupActor")
  val eventActor = context.actorSelection("/user/eventActor")

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
        } ~
        pathPrefix("joinGroup") {
          path(Segment / Segment) { (userId, groupId) =>
            put { requestContext =>
              val responder = createResponder(requestContext)
              val future = groupActor ? JoinUserGroup(userId, groupId)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Group Joined."
                case _ => responder ! UserOpFailed
              }
            }
          }
        } ~
        pathPrefix("leaveGroup") {
          path(Segment / Segment) { (userId, groupId) =>
            put { requestContext =>
              val responder = createResponder(requestContext)
              val future = groupActor ? LeaveUserGroup(userId, groupId)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Group Left."
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
          } ~
          pathPrefix("addGroup") {
            path(Segment / Segment) { (albumId, groupId) =>
              put { requestContext =>
                val responder = createResponder(requestContext)
                val future = groupActor ? AddAlbumGroup(albumId.toInt, groupId)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Add album to group."
                  case false => responder ! AlbumOpFailed
                }
              }
            }
          } ~
          pathPrefix("removeGroup") {
            path(Segment / Segment) { (albumId, groupId) =>
              put { requestContext =>
                val responder = createResponder(requestContext)
                val future = groupActor ? RemoveAlbumGroup(albumId.toInt, groupId)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Remove album to group."
                  case false => responder ! AlbumOpFailed
                }
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
      } ~
      pathPrefix("profile") {
        pathEnd {
          post {
            entity(as[Profile]) { profile => requestContext =>
              log.info("Get profile creation request: {}", profile)
              val responder = createResponder(requestContext)
              val future = profileActor ? CreateProfile(profile)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Profile created."
                case false => responder ! ProfileOpFailed
              }
            }
          }
        } ~
          path(Segment) { profileId =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              val future = profileActor ? GetProfile(profileId.toInt)
              Await.result(future, timeout.duration).asInstanceOf[Option[Profile]] match {
                case Some(profile: Profile) => profile.fbType match {
                  case "user" =>
                    val future = userActor ? GetUser(profile.fbId)
                    Await.result(future, timeout.duration).asInstanceOf[Option[User]] match {
                      case Some(user: User) => responder ! user
                      case None => responder ! ProfileOpFailed
                    }
                  case "group" =>
                    val future = groupActor ? GetGroup(profile.fbId)
                    Await.result(future, timeout.duration).asInstanceOf[Option[Group]] match {
                      case Some(group: Group) => responder ! group
                      case None => responder ! ProfileOpFailed
                    }
                  // TODO: event
                }
                case None => responder ! PictureOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = profileActor ? DeleteProfile(profileId.toInt)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Profile Deleted."
                  case false => responder ! ProfileOpFailed
                }
              }
          }
      } ~
      pathPrefix("group") {
        pathEnd {
          post {
            entity(as[Group]) { group => requestContext =>
              log.info("Get group creation request: {}", group)
              val responder = createResponder(requestContext)
              val future = groupActor ? CreateGroup(group)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Group created."
                case false => responder ! GroupOpFailed
              }
            }
          }
        } ~
          path(Segment) { groupId =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              val future = groupActor ? GetGroup(groupId)
              Await.result(future, timeout.duration).asInstanceOf[Option[Group]] match {
                case Some(group: Group) => responder ! group
                case None => responder ! GroupOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = groupActor ? DeleteGroup(groupId)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Group Deleted."
                  case false => responder ! GroupOpFailed
                }
              }
          }
      } ~
      pathPrefix("event") {
        pathEnd {
          post {
            entity(as[Event]) { event => requestContext =>
              log.info("Get event creation request: {}", event)
              val responder = createResponder(requestContext)
              val future = eventActor ? CreateEvent(event)
              Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                case true => responder ! "Event created."
                case false => responder ! EventOpFailed
              }
            }
          }
        } ~
          path(Segment) { eventId =>
            get { requestContext =>
              val responder = createResponder(requestContext)
              val future = eventActor ? GetEvent(eventId.toInt)
              Await.result(future, timeout.duration).asInstanceOf[Option[Event]] match {
                case Some(event: Event) => responder ! event
                case None => responder ! GroupOpFailed
              }
            } ~
              delete { requestContext =>
                val responder = createResponder(requestContext)
                val future = eventActor ? DeleteEvent(eventId.toInt)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Event Deleted."
                  case false => responder ! EventOpFailed
                }
              }
          } ~
          pathPrefix("addGroup") {
            path(Segment / Segment) { (eventId, groupId) =>
              put { requestContext =>
                val responder = createResponder(requestContext)
                val future = groupActor ? AddEventGroup(eventId.toInt, groupId)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Add event to group."
                  case false => responder ! EventOpFailed
                }
              }
            }
          } ~
          pathPrefix("removeGroup") {
            path(Segment / Segment) { (eventId, groupId) =>
              put { requestContext =>
                val responder = createResponder(requestContext)
                val future = groupActor ? RemoveEventGroup(eventId.toInt, groupId)
                Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
                  case true => responder ! "Remove event to group."
                  case false => responder ! EventOpFailed
                }
              }
            }
          }
      }


  private def createResponder(requestContext: RequestContext): ActorRef = {
    context.actorOf(Props(new Responder(requestContext)))
  }

}