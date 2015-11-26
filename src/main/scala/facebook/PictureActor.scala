package facebook

import akka.pattern.ask
import scala.concurrent.Await

/**
  * Created by xiaoyong on 11/25/2015.
  */
class PictureActor extends BasicActor {

  import Structures._

  private val pictures = scala.collection.mutable.Map[Int, Picture]()
  private var pictureCount = 0

  def createPicture(picture: Picture): Boolean = {
    val future = albumActor ? AddPicture(picture.albumId, pictureCount)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        pictures += (pictureCount -> picture)
        pictureCount += 1
        true
      case false =>
        false
    }
  }

  def getPicture(pictureId: Int): Option[Picture] = {
    if (!pictures.contains(pictureId))
      return None
    Some(pictures(pictureId))
  }

  def deletePicture(pictureId: Int): Boolean = {
    if (!pictures.contains(pictureId))
      return false
    val picture = pictures(pictureId)
    pictures -= pictureId
    val future = albumActor ? RemovePicture(picture.albumId, pictureId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case false =>
        log.warning("Picture {} can't be removed from album side.", picture.albumId)
    }
    true
  }

  def receive = {
    case CreatePicture(picture: Picture) =>
      sender ! createPicture(picture)
    case GetPicture(pictureId: Int) =>
      sender ! getPicture(pictureId)
    case DeletePicture(pictureId: Int) =>
      sender ! deletePicture(pictureId)
  }
}
