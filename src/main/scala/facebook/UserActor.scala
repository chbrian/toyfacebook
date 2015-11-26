package facebook

import akka.actor.{PoisonPill, Actor}
import facebook.Structures._
import spray.http.StatusCodes
import spray.routing.RequestContext

/**
  * Created by xiaoyong on 11/25/2015.
  */
class UserActor extends Actor {

  private val users = scala.collection.mutable.Map[String, User]()

  def createUser(user: User): Boolean = {
    if (users.contains(user.id))
      return false
    users += (user.id -> new User(user.id, user.name, user.password))
    true
  }

  def getUser(id: String): Option[User] = {
    if (!users.contains(id))
      return None
    Some(users(id))
  }

  def deleteUser(id: String): Boolean = {
    if (!users.contains(id))
      return false
    users -= id
    // TODO delete all the friend references
    // TODO delete all post by this user
    true
  }

  // Friend Actions
  def addFriend(id: String, friendId: String): Boolean = {
    if (!users.contains(id) || !users.contains(friendId))
      return false
    if (!users(id).friends.contains(friendId))
      users(id).friends.append(friendId)
    true
  }

  def removeFriend(id: String, friendId: String): Boolean = {
    if (!users.contains(id) || !users.contains(friendId) || !users(id).friends.contains(friendId))
      return false
    users(id).friends -= friendId
    true
  }

  // Post Actions
  def addPost(id: String, postId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).posts.append(postId)
    true
  }

  def deletePost(id: String, postId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).posts -= postId
    true
  }

  // Album Actions
  def addAlbum(id: String, albumId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).albums.append(albumId)
    true
  }

  def removeAlbum(id: String, albumId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).albums -= albumId
    true
  }

  def receive = {
    case CreateUser(user: User) =>
      sender ! createUser(user)

    case GetUser(id: String) =>
      sender ! getUser(id)

    case DeleteUser(id: String) =>
      sender ! deleteUser(id)

    case AddFriend(id: String, friendId: String) =>
      sender ! addFriend(id, friendId)

    case RemoveFriend(id: String, friendId: String) =>
      sender ! removeFriend(id, friendId)

    case AddPost(id: String, postId: Int) =>
      sender ! addPost(id, postId)

    case RemovePost(id: String, postId: Int) =>
      sender ! deletePost(id, postId)

    case AddAlbum(id: String, albumId: Int) =>
      sender ! addAlbum(id, albumId)

    case RemoveAlbum(id: String, albumId: Int) =>
      sender ! removeAlbum(id, albumId)
  }
}