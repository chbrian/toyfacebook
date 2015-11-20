package fb

/**
  * Handle all the Posts.
  * Created by alan on 11/20/15.
  */
class Posts {

  import Structures._

  private val posts = scala.collection.mutable.Map[Int, Post]()
  private var postCount = 0

  def createPost(post: Post): Int = {
    posts += (postCount -> post)
    postCount += 1
    postCount - 1
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
    Some(post)
  }
}
