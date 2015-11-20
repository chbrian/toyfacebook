package fb

import scala.collection.mutable.ArrayBuffer

/**
  * Defining User structure
  * Created by alan on 11/17/2015.
  */
object Structures {

  // FB REST post requests
  case class Post(id: Int, content: String, owner_id: String)

  case object PostCreated

  case object PostDeleted

  case object PostOpFailed

  // FB REST user requests
  case class User(id: String, name: String, password: String)

  case object UserCreated

  case object UserDeleted

  case object UserOpFailed

  // FB REST user friend request
  case class Friend(A: String, B: String)

  case object FriendAdded

  case object FriendOpFailed

  // UserInfo stored in server
  class UserInfo(uid: String, uname: String, upassword: String) {
    val id = uid
    val name = uname
    val password = upassword
    val friends = new ArrayBuffer[String]()
    val page = new ArrayBuffer[Int]()

    def getInfo: UserInfo = {
      val info = new UserInfo(id, name, null)
      info.friends.appendAll(friends)
      info.page.appendAll(page)
      info
    }
  }

  /* json (un)marshalling */

  import spray.json._

  // Json for Post
  object Post extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(Post.apply)
  }

  // Json for User
  object User extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(User.apply)
  }

  // Json for Friend
  object Friend extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Friend.apply)
  }

  // Json for UserInfo
  object UserInfo extends DefaultJsonProtocol {
    implicit object UserInfoJsonFormat extends RootJsonFormat[UserInfo] {
      def write(ur: UserInfo) = JsObject(
        Map(
          "id" -> JsString(ur.id),
          "name" -> JsString(ur.name),
          "friends" -> JsArray(ur.friends.map(_.toJson).toVector),
          "page" -> JsArray(ur.page.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("id", "name", "friends", "page") match {
          case Seq(JsString(id), JsString(name), JsArray(friends), JsArray(page)) =>
            new UserInfo(id, name, null)
          case _ => throw new DeserializationException("UserInfo expected")
        }
      }
    }
  }

}
