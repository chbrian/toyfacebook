//package fb
//
///**
//  * Created by xiaoyong on 11/25/2015.
//  */
//class Albums {
//  import Structures._
//
//  private val albums = scala.collection.mutable.Map[Int, Album]()
//  private var albumCount = 0
//
//  def createAlbum(album: Album): Int = {
//    albums += (albumCount -> album)
//    albumCount += 1
//    albumCount - 1
//  }
//
//  def getAlbum(albumId: Int): Option[Album] = {
//    if (!albums.contains(albumId))
//      return None
//    Some(albums(albumId))
//  }
//
//  def deletePost(postId: Int): Option[Album] = {
//    if (!albums.contains(postId))
//      return None
//    val post = albums(postId)
//    albums -= postId
//    albumCount -= 1
//    Some(post)
//  }
//}
