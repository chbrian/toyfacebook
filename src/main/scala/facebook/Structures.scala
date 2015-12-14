package facebook

import spray.httpx.SprayJsonSupport
import spray.json._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Defining User structure
  * Created by alan on 11/17/2015.
  */
object Structures {

  // create/get/delete is for object
  // add/remove is for user

  // FB REST user requests

  case class User(id: String, name: String, publicKey: Array[Byte], friends: ArrayBuffer[String] = new ArrayBuffer[String](),
                  posts: ArrayBuffer[Int] = new ArrayBuffer[Int](), albums: ArrayBuffer[Int] = new ArrayBuffer[Int](),
                  groups: ArrayBuffer[String] = new ArrayBuffer[String](),
                  events: ArrayBuffer[Int] = new ArrayBuffer[Int]())

  case class CreateUser(user: User)

  case class GetUser(userId: String)

  case class DeleteUser(userId: String)

  case class AddFriend(userId: String, friendId: String)

  case class RemoveFriend(userId: String, friendId: String)


  case object UserOpFailed

  // FB REST post requests
  case class Post(userId: String, encryptedContent: Array[Byte], userList: ArrayBuffer[String], keyList: ArrayBuffer[Array[Byte]])

  case class CreatePost(post: Post)

  case class GetPost(postId: Int)

  case class DeletePost(postId: Int)

  case class AddPost(userId: String, postId: Int)

  case class RemovePost(userId: String, postId: Int)

  case object PostOpFailed

  // FB REST profile requests
  case class Profile(fbType: String, fbId: String)

  case class CreateProfile(profile: Profile)

  case class GetProfile(profileId: Int)

  case class DeleteProfile(profileId: Int)

  case object ProfileOpFailed

  // FB REST album requests
  case class Album(ownerId: String, name: String, pictures: ArrayBuffer[Int] = new ArrayBuffer[Int]())

  case class CreateAlbum(album: Album)

  case class GetAlbum(albumId: Int)

  case class DeleteAlbum(albumId: Int)

  case class AddAlbum(ownerId: String, albumId: Int)

  case class RemoveAlbum(ownerId: String, albumId: Int)

  case class AddPicture(albumId: Int, pictureId: Int)

  case class RemovePicture(albumId: Int, pictureId: Int)

  case object AlbumOpFailed

  // FB REST picture requests
  case class Picture(albumId: Int, name: String, content: ArrayBuffer[Byte] = new ArrayBuffer[Byte]())

  case class CreatePicture(picture: Picture)

  case class GetPicture(pictureId: Int)

  case class DeletePicture(pictureId: Int)

  case object PictureOpFailed

  // FB REST group requests
  case class Group(id: String, userId: String, name: String, members: ArrayBuffer[String] = new ArrayBuffer[String](),
                   albums: ArrayBuffer[Int] = new ArrayBuffer[Int](), events: ArrayBuffer[Int] = new ArrayBuffer[Int]())

  case class CreateGroup(group: Group)

  case class GetGroup(groupId: String)

  case class DeleteGroup(groupId: String)

  // a user joins in a group
  case class JoinUserGroup(userId: String, groupId: String)

  case class LeaveUserGroup(userId: String, groupId: String)

  // a event add to a group
  case class AddEventGroup(eventId: Int, groupId: String)

  case class RemoveEventGroup(eventId: Int, groupId: String)

  // an album add to a group
  case class AddAlbumGroup(albumId: Int, groupId: String)

  case class RemoveAlbumGroup(albumId: Int, groupId: String)

  case object GroupOpFailed

  // FB REST event requests
  case class Event(userId: String, name: String, time: String,
                   attending: ArrayBuffer[String] = new ArrayBuffer[String]())

  case class CreateEvent(event: Event)

  case class GetEvent(eventId: Int)

  case class DeleteEvent(eventId: Int)

  case class AttendEvent(userId: String, eventId: Int)

  case class CancelEvent(userId: String, eventId: Int)

  case object EventOpFailed

  // FB REST key requests
  case class GetFriendKey(userId: String)

  case object KeyOpFailed

  //  case class UserKeyMap(userList: Array[String], keyList: Array[Array[Byte]])
  //
  //
  //  // Json for UserKeyMap
  //  object UserKeyMap extends DefaultJsonProtocol {
  //
  //    implicit object UserKeyMapsonFormat extends RootJsonFormat[UserKeyMap] {
  //      def write(userKeyMap: UserKeyMap) = JsObject(
  //        Map(
  //          "userList" -> JsArray(userKeyMap.userList.map(_.toJson).toVector),
  //          "keyList" -> JsArray(userKeyMap.keyList.map(_.toJson).toVector)
  //        )
  //
  //      )
  //
  //      def read(value: JsValue) = {
  //        value.asJsObject.getFields("userList", "keyList") match {
  //          case Seq(JsArray(userList), JsArray(keyList)) =>
  //            new UserKeyMap(userList.map(_.toString).toArray, keyList.map(_.convertTo[Array[Byte]]).toArray)
  //        }
  //      }
  //    }
  //
  //  }

  // Json for User
  object User extends DefaultJsonProtocol {

    implicit object UserJsonFormat extends RootJsonFormat[User] {
      def write(user: User) = JsObject(
        Map(
          "id" -> JsString(user.id),
          "name" -> JsString(user.name),
          "publicKey" -> JsArray(user.publicKey.map(_.toJson).toVector),
          "friends" -> JsArray(user.friends.map(_.toJson).toVector),
          "posts" -> JsArray(user.posts.map(_.toJson).toVector),
          "albums" -> JsArray(user.albums.map(_.toJson).toVector),
          "groups" -> JsArray(user.groups.map(_.toJson).toVector),
          "events" -> JsArray(user.events.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("id", "name", "publicKey", "friends", "posts", "albums", "groups", "events") match {
          case Seq(JsString(id), JsString(name), JsArray(publicKey)) =>
            new User(id, name, publicKey.map(_.toString.toByte).toArray)
          case Seq(JsString(id), JsString(name), JsArray(publicKey), JsArray(friends), JsArray(posts), JsArray(albums),
          JsArray(groups), JsArray(events)) =>
            new User(id, name, publicKey.map(_.toString.toByte).toArray, friends.map(x => x.toString).to[ArrayBuffer],
              posts.map(x => x.toString.toInt).to[ArrayBuffer], albums.map(x => x.convertTo[Int]).to[ArrayBuffer],
              groups.map(x => x.toString).to[ArrayBuffer], events.map(x => x.convertTo[Int]).to[ArrayBuffer])
          case _ => throw new DeserializationException("User expected")
        }
      }
    }

  }

  // Json for Post
  object Post extends DefaultJsonProtocol {

    implicit object PostJsonFormat extends RootJsonFormat[Post] {
      def write(post: Post) = JsObject(
        Map(
          "userId" -> JsString(post.userId),
          "encryptedContent" -> JsArray(post.encryptedContent.map(_.toJson).toVector),
          "userList" -> JsArray(post.userList.map(_.toJson).toVector),
          "keyList" -> JsArray(post.keyList.map(x => JsArray(x.map(_.toJson).toVector)).toVector)
        )

      )

//      def read(value: JsValue): Post = ???

        def read(value: JsValue) = {
          value.asJsObject.getFields("userId", "encryptedContent", "userList", "keyList") match {
            case Seq(JsString(userId), JsArray(encryptedContent), JsArray(userList), JsArray(keyList)) =>
              new Post(userId, encryptedContent.map(_.toString.toByte).toArray,
                userList.map(_.convertTo[String]).to[ArrayBuffer], keyList.map(_.convertTo[Array[Byte]]).to[ArrayBuffer])
            case _ => throw new DeserializationException("Post expected")
          }
        }
    }

  }

  // Json for Profile
  object Profile extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Profile.apply)
  }

  // Json for Album
  object Album extends DefaultJsonProtocol {

    implicit object AlbumJsonFormat extends RootJsonFormat[Album] {
      def write(album: Album) = JsObject(
        Map(
          "ownerId" -> JsString(album.ownerId),
          "name" -> JsString(album.name),
          "pictures" -> JsArray(album.pictures.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("ownerId", "name", "pictures") match {
          case Seq(JsString(ownerId), JsString(name)) =>
            new Album(ownerId, name)
          case Seq(JsString(ownerId), JsString(name), JsArray(pictures)) =>
            new Album(ownerId, name, pictures.map(x => x.convertTo[Int]).to[ArrayBuffer])
          case _ => throw new DeserializationException("Album expected")
        }
      }
    }

  }

  // Json for Picture
  object Picture extends DefaultJsonProtocol {

    implicit object PictureJsonFormat extends RootJsonFormat[Picture] {
      def write(picture: Picture) = JsObject(
        Map(
          "albumId" -> JsNumber(picture.albumId),
          "name" -> JsString(picture.name),
          "content" -> JsArray(picture.content.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("albumId", "name", "content") match {
          case Seq(JsNumber(albumId), JsString(name)) =>
            new Picture(albumId.toInt, name)
          case Seq(JsNumber(albumId), JsString(name), JsArray(content)) =>
            new Picture(albumId.toInt, name, content.map(_.convertTo[Byte]).to[ArrayBuffer])
          case _ => throw new DeserializationException("Picture expected")
        }
      }
    }

  }

  // Json for Group
  object Group extends DefaultJsonProtocol {

    implicit object GroupJsonFormat extends RootJsonFormat[Group] {
      //          id: String, userId: String, name: String, members: ArrayBuffer[String] = new ArrayBuffer[String](),
      //        albums: ArrayBuffer[Int] = new ArrayBuffer[Int](), events
      def write(group: Group) = JsObject(
        Map(
          "id" -> JsString(group.id),
          "userId" -> JsString(group.userId),
          "name" -> JsString(group.name),
          "members" -> JsArray(group.members.map(_.toJson).toVector),
          "albums" -> JsArray(group.albums.map(_.toJson).toVector),
          "events" -> JsArray(group.events.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("id", "userId", "name", "members", "albums", "events") match {
          case Seq(JsString(id), JsString(userId), JsString(name)) =>
            new Group(id, userId, name)
          case Seq(JsString(id), JsString(userId), JsString(name), JsArray(members), JsArray(albums),
          JsArray(events)) =>
            new Group(id, userId, name, members.map(x => x.toString).to[ArrayBuffer],
              albums.map(x => x.toString.toInt).to[ArrayBuffer], events.map(x => x.convertTo[Int]).to[ArrayBuffer])
          case _ => throw new DeserializationException("Group expected")
        }
      }
    }

  }

  // Json for Event
  object Event extends DefaultJsonProtocol {

    implicit object EventJsonFormat extends RootJsonFormat[Event] {
      def write(event: Event) = JsObject(
        Map(
          "userId" -> JsString(event.userId),
          "name" -> JsString(event.name),
          "time" -> JsString(event.time),
          "attending" -> JsArray(event.attending.map(_.toJson).toVector)
        )
      )

      def read(value: JsValue) = {
        value.asJsObject.getFields("userId", "name", "time", "attending") match {
          case Seq(JsString(userId), JsString(name), JsString(time)) =>
            new Event(userId, name, time)
          case Seq(JsString(userId), JsString(name), JsString(time), JsArray(attending)) =>
            new Event(userId, name, time, attending.map(x => x.toString).to[ArrayBuffer])
          case _ => throw new DeserializationException("Event expected")
        }
      }
    }

  }

  object NestedJsonProtocol extends DefaultJsonProtocol {

    type StringToAny = Map[String, Any]

    implicit object MapJsonFormat  extends JsonFormat[Map[String, Any] ] {
      def write(m: Map[String, Any]) = {
      JsObject(m.mapValues {
      case v: String => JsString(v)
      case v: Int => JsNumber(v)
      case v: StringToAny => write(v)
      case v: Any => JsString(v.toString)
      })
      }

      def read(value: JsValue) = ???
    }
  }



}
