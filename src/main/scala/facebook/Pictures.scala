//package facebook
//
///**
//  * Created by xiaoyong on 11/25/2015.
//  */
//class Pictures {
//  import Structures._
//  import fb.RestApi
//
//  private val pictures = scala.collection.mutable.Map[Int, Picture]()
//  private var pictureCount = 0
//
//  def createPicture(picture: Picture): Int ={
//    val album = RestApi.ALBUMS
//
//    getAlbum(picture.albumId)
//    pictures += (pictureCount -> picture)
//    pictureCount += 1
//    pictureCount -1
//
//    // add Picture to an Album
//
//  }
//
//  def getPicture(pictureId: Int): Option[Picture] = {
//    if (!pictures.contains(pictureId))
//      return None
//    Some(pictures(pictureId))
//  }
//
//  def deletePicture(pictureId: Int): Boolean= {
//    if (!pictures.contains(pictureId))
//      return false
//    else{
//      pictures.remove(pictureId)
//      pictureCount -= 1
//      return true
//    }
//  }
//}
