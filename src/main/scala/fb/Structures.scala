package fb

/**
 * Defining User structure
 * Created by alan on 11/17/2015.
 */
object Structures {

  case class Post(id: Int, content: String, owner_id: String)
  case object PostCreated
  case object PostDeleted
  case object PostOpFailed

  case class User(id: String, name: String, password: String)
  case object UserCreated
  case object UserDeleted
  case object UserOpFailed

  case class Friend(A: String, B: String)
  case object FriendAdded
  case object FriendOpFailed

  /* json (un)marshalling */
  import spray.json._

  object Post extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(Post.apply)
  }

  object User extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(User.apply)
  }

  object Friend extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Friend.apply)
  }
}
