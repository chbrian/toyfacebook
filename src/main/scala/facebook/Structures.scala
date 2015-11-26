package facebook

import java.awt.image.BufferedImage

import spray.httpx.SprayJsonSupport
import spray.json._
import scala.collection.mutable.ArrayBuffer
/**
  * Defining User structure
  * Created by alan on 11/17/2015.
  */
object Structures {

  // FB REST user requests

  case class User(id: String, name: String, password: String, friends: ArrayBuffer[String]=new ArrayBuffer[String](),
                  posts: ArrayBuffer[Int]=new ArrayBuffer[Int](), albums: ArrayBuffer[Int]=new ArrayBuffer[Int]())

  case class CreateUser(user: User)

  case class GetUser(id: String)

  case class DeleteUser(id: String)

  case object UserCreated

  case object UserDeleted

  case object UserOpFailed

  // FB REST post requests
  case class Post(ownerId: String, content: String)

  case class CreatePost(post: Post)

  case class GetPost(id: Int)

  case class DeletePost(id: Int)

  case class AddPost(id: String, postId: Int) // for user

  case class RemovePost(id: String, postId: Int) // for user

  case object PostCreated

  case object PostDeleted

  case object PostOpFailed

  // FB REST picture requests
  case class Picture(albumId: Int, name: String, content: BufferedImage)

  case object PictureCreated

  case object PictureDeleted

  case object PictureOpFailed

  // FB REST album requests
  case class Album(ownerId: String, name: String)

  case object AlbumCreated

  case object AlbumDeleted

  case object AlbumOpFailed


  // FB REST user friend request
  case object FriendAdded

  case object FriendOpFailed


    // Json for UserInfo
  object User extends DefaultJsonProtocol {
    implicit object UserJsonFormat extends RootJsonFormat[User] {
      def write(ur: User) = JsObject(
        Map(
          "id" -> JsString(ur.id),
          "name" -> JsString(ur.name),
          "password" -> JsString(ur.password),
          "friends" -> JsArray(ur.friends.map(_.toJson).toVector),
          "posts" -> JsArray(ur.posts.map(_.toJson).toVector),
          "albums" -> JsArray(ur.albums.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("id", "name", "password", "friends", "posts", "albums") match {
          case Seq(JsString(id), JsString(name), JsString(password)) =>
            new User(id, name, password)
          case Seq(JsString(id), JsString(name), JsString(password), JsArray(friends), JsArray(posts), JsArray(albums)) =>
            new User(id, name, password)
          case _ => throw new DeserializationException("User expected")
        }
      }
    }
  }

  // Json for Post
  object Post extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Post.apply)
  }
}




//}
