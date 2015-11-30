package client


import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.event.Logging
import akka.util.Timeout
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
  * Client App for benchmarking.
  * Created by alan on 11/24/2015.
  * Modified by xiaohui on 11/28/2015.
  * Modified by xiaoyong on 11/29/2015.
  */
object Main extends App with Requests {

  case class CreateUser(id: String)

  case class GetUser(id: String)

  case class DeleteUser(id: String)

  case class CreateGroup(id: String)

  case class GetGroup(id: String)

  case class DeleteGroup(id: String)

  case class CreatePost(userId: String)

  case class GetPost(postId: Int)

  case class DeletePost(postId: Int)

  case class CreateAlbum(userId: String)

  case class GetAlbum(albumId: Int)

  case class DeleteAlbum(albumId: Int)

  case class CreatePicture(albumId: Int, location: String)

  case class GetPicture(albumId: Int)

  case class DeletePicture(albumId: Int)

  // Create an ActorSystem to host our client application in
  implicit val system = ActorSystem("client")

  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.shutdown())
  implicit val timeout: Timeout = 60.seconds

  val scala = 3
  val alphabetList = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList

  val idList = alphabetList.combinations(scala).toList


  // user creation test
  def createUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (userTester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      userTester ! CreateUser(sb.toString)
      index += 1
    }
  }

  // user getter test
  def getUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (userTester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      userTester ! GetUser(sb.toString)
      index += 1
    }
  }

  //  user delete test
  def deleteUser(): Unit = {
    val testerArray = (1 to userTestScale).map(x => system.actorOf(Props[Tester]))

    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! DeleteUser(sb.toString)
      index += 1
    }

  }

  // group creation test
  def createGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateGroup(sb.toString)
      index += 1
    }
  }

  // group getter test
  def getGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! GetGroup(sb.toString)
      index += 1
    }
  }

  //group delete test
  def deleteGroup: Unit = {
    val testerArray = (1 to groupTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! DeleteGroup(sb.toString)
      index += 1
    }
  }

  // post creation test, each user create two posts
  def createPost: Unit = {
    val testerArray = (1 to postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreatePost(sb.toString)
      tester ! CreatePost(sb.toString)
      index += 1
    }
  }

  // post getter test
  def getPost: Unit = {
    val testerArray = (1 to 2 * postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetPost(index)
      index += 1
    }
  }

  // post delete test
  def deletePost: Unit = {
    val testerArray = (1 to 2 * postTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeletePost(index)
      index += 1
    }
  }

  // album creation test, each user create an album
  def createAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      val sb = new StringBuilder
      idList(index).map(x => sb.append(x))
      tester ! CreateAlbum(sb.toString)
      index += 1
    }
  }

  // album getter test
  def getAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetAlbum(index)
      index += 1
    }
  }

  // album delete test
  def deleteAlbum: Unit = {
    val testerArray = (1 to albumTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeleteAlbum(index)
      index += 1
    }
  }

  // picture creation test, each album create one picture, the picture is uploaded in the client side
  def createPicture: Unit = {
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    val location = "src\\main\\scala\\client\\test2.jpg"
    for (tester <- testerArray) {
      tester ! CreatePicture(index, location)
      index += 1
    }
  }

  // picture get test
  def getPicture: Unit ={
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! GetPicture(index)
      index += 1
    }
  }

  // picture delete test
  def deletePicture: Unit ={
    val testerArray = (1 to pictureTestScale).map(x => system.actorOf(Props[Tester]))
    var index = 0
    for (tester <- testerArray) {
      tester ! DeletePicture(index)
      index += 1
    }
  }


  val userTestScale = 1
  val groupTestScale = 1
  val postTestScale = 1
  val albumTestScale = 1
  val pictureTestScale = 1

  createUser
  Thread sleep 1000
  getUser
  Thread sleep 1000
  //  deleteUser
  //  Thread sleep 1000
//  createGroup
//  Thread sleep 1000
//  getGroup
//  Thread sleep 1000
//  deleteGroup
//  Thread sleep 1000
//  createPost
//  Thread sleep 1000
//  getPost
//  Thread sleep 1000
//  deletePost
//  Thread sleep 1000
  createAlbum
  Thread sleep 1000
  getAlbum
  Thread sleep 1000
//  deleteAlbum
//  Thread sleep 1000
  createPicture
  Thread sleep 1000
  getPicture
  Thread sleep 1000
  deletePicture
  Thread sleep 1000


}
