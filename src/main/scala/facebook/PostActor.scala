package facebook

import akka.pattern.ask
import scala.concurrent.Await

/**
  * Handle all the Posts.
  * Created by alan on 11/20/15.
  */
class PostActor extends BasicActor{

  import Structures._

  private val posts = scala.collection.mutable.Map[Int, Post]()
  private var postCount = 0

  def createPost(post: Post): Boolean = {
    posts += (postCount -> post)
    val future = userActor ? AddPost(post.ownerId, postCount)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        postCount += 1
        true
      case false =>
        false
    }
  }

  def getPost(postId: Int): Option[Post] = {
    if (!posts.contains(postId))
      return None
    Some(posts(postId))
  }

  def deletePost(postId: Int): Option[Post] = {
    if (!posts.contains(postId))
      return None
    val post = posts(postId)
    posts -= postId
    val future = userActor ? RemovePost(post.ownerId, postId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case false =>
        log.warning("Post {} can't be removed from user side.", postId)
    }
    Some(post)
  }

  def receive = {
    case CreatePost(post: Post) =>
      sender ! createPost(post)

    case GetPost(postId: Int) =>
      sender ! getPost(postId)

    case DeletePost(postId: Int) =>
      sender ! deletePost(postId)
  }
}
