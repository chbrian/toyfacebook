package fb

import scala.collection.mutable.ArrayBuffer

/**
  * Defining User structure
  * Created by alan on 11/17/2015.
  */
object Structures {

  // FB REST post requests
  case class Post(ownerId: String, content: String)

  case object PostCreated

  case object PostDeleted

  case object PostOpFailed

  // FB REST user requests
  case class User(id: String, name: String, password: String)

  case object UserCreated

  case object UserDeleted

  case object UserOpFailed

  // FB REST user friend request
  case object FriendAdded

  case object FriendOpFailed

  // UserInfo stored in server
  class UserInfo(uid: String, uname: String, upassword: String) {
    val id = uid
    val name = uname
    val password = upassword
    val friends = new ArrayBuffer[String]()
    val posts = new ArrayBuffer[Int]()
    val albums = new ArrayBuffer[Int]()

    def getInfo: UserInfo = {
      val info = new UserInfo(id, name, null)
      info.friends.appendAll(friends)
      info.posts.appendAll(posts)
      info.albums.appendAll(albums)
      info
    }
  }

  /* json (un)marshalling */

  import spray.json._

  // Json for Post
  object Post extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Post.apply)
  }

  // Json for User
  object User extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(User.apply)
  }

  // Json for UserInfo
  object UserInfo extends DefaultJsonProtocol {
    implicit object UserInfoJsonFormat extends RootJsonFormat[UserInfo] {
      def write(ur: UserInfo) = JsObject(
        Map(
          "id" -> JsString(ur.id),
          "name" -> JsString(ur.name),
          "friends" -> JsArray(ur.friends.map(_.toJson).toVector),
          "posts" -> JsArray(ur.posts.map(_.toJson).toVector),
          "albums" -> JsArray(ur.albums.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("id", "name", "friends", "posts", "albums") match {
          case Seq(JsString(id), JsString(name), JsArray(friends), JsArray(posts), JsArray(albums)) =>
            new UserInfo(id, name, null)
          case _ => throw new DeserializationException("UserInfo expected")
        }
      }
    }
  }

}
