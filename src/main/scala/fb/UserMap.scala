package fb

import scala.collection.mutable.ArrayBuffer

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
    users(id).friends.append(friendId)
    true
  }

  def getFriends(id: String): Option[Seq[String]] = {
    if (!users.contains(id))
      return None
    Some(users(id).friends.toSeq)
  }
}

class UserInfo(uid: String, uname: String, upassword: String) {
  val id = uid
  val name = uname
  val password = upassword
  val friends = new ArrayBuffer[String]()
  val posts = new ArrayBuffer[Int]()
}