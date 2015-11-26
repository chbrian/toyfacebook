package facebook

import akka.pattern.ask
import scala.concurrent.Await

/**
  * Created by xiaoyong on 11/25/2015.
  */
class AlbumActor extends BasicActor {

  import Structures._

  private val albums = scala.collection.mutable.Map[Int, Album]()
  private var albumCount = 0

  def createAlbum(album: Album): Boolean = {
    val future = userActor ? AddAlbum(album.ownerId, albumCount)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        albums += (albumCount -> album)
        albumCount += 1
        true
      case false =>
        false
    }
  }

  def getAlbum(albumId: Int): Option[Album] = {
    if (!albums.contains(albumId))
      return None
    Some(albums(albumId))
  }

  def deleteAlbum(albumId: Int): Boolean = {
    // TODO delete pictures in the album.
    if (!albums.contains(albumId))
      return false
    val album = albums(albumId)
    albums -= albumId
    val future = userActor ? RemoveAlbum(album.ownerId, albumId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case false =>
        log.warning("Album {} can't be removed from user side.", albumId)
    }
    true
  }

  def receive = {
    case CreateAlbum(album: Album) =>
      sender ! createAlbum(album)
    case GetAlbum(albumId: Int) =>
      sender ! getAlbum(albumId)
    case DeleteAlbum(albumId: Int) =>
      sender ! deleteAlbum(albumId)

  }
}
