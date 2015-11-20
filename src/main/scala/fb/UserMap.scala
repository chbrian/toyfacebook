package fb

/**
  * Handle the set of users.
  * Created by alan on 11/17/2015.
  */
class UserMap {

  import Structures._

  private val users = scala.collection.mutable.Map[String, UserInfo]()

  def createUser(user: User): Boolean = {
    if (users.contains(user.id))
      return false
    users += (user.id -> new UserInfo(user.id, user.name, user.password))
    true
  }

  def getUser(id: String): Option[String] = {
    if (!users.contains(id))
      return None
    Some(users(id).name)
  }

  def getUserInfo(id: String): Option[UserInfo] = {
    if (!users.contains(id))
      return None
    Some(users(id).getInfo)
  }

  def deleteUser(id: String): Boolean = {
    if (!users.contains(id))
      return false
    users -= id
    // Do we need to also delete all the friend references?
    true
  }

  def addFriend(id: String, friendId: String): Boolean = {
    if (!users.contains(id) || !users.contains(friendId))
      return false
    if (!users(id).friends.contains(friendId))
      users(id).friends.append(friendId)
    true
  }

  def addPost(id: String, postId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).posts.append(postId)
    true
  }

  def removePost(id: String, postId: Int): Boolean = {
    if (!users.contains(id))
      return false
    users(id).posts -= postId
    true
  }
}
