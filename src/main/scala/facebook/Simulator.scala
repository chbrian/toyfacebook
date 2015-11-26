//package facebook
//
//import akka.actor.{PoisonPill, Actor}
//import facebook.Structures._
//import facebook.UserActor._
//import spray.http.StatusCodes
//import spray.routing.RequestContext
//
///**
//  * Created by xiaohui on 11/25/2015.
//  */
//
//
//object Boot extends App with Configuration {
//
//  // create an actor system for application
//  implicit val system = ActorSystem("fb-simulator")
//
//  // create and start rest service actor
//  val restService = system.actorOf(Props[RestServiceActor], "rest-endpoint")
//
//  // start HTTP server with rest service actor as a handler
//  IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
//
//  //Use POST with json to create user
//  curl -X POST http://localhost:8080/user -H "Content-Type: application/json" -d '{"id":"yi", "name":"yi","password":"123"}'
//
//  //Use DELETE to delete user
//  curl -X DELETE http://localhost:8080/user/yi
//
//  //User PUT to add friend
//  curl -X PUT http://localhost:8080/addfriend/yi/yuan
//
//  //Use GET to get userinfo by id
//  curl -X GET http://localhost:8080/user/yi
//
//  //User PUT to add new post
//  curl -X PUT http://localhost:8080/addpost/happyholiday
//
//  //Use DELETE to remove post
//  curl -X DELETE http://localhost:8080/removepost/happyholiday
//
//  //User PUT to upload a picture
//  curl -X PUT http://localhost:8080/createpicture/gator
//
//  //Use GET to get picture by id
//  curl -X GET http://localhost:8080/getpicture/gator
//
//  //Use DELETE to delete a picture
//  curl -X DELETE http://localhost:8080/deletepicture/gator
//
//
//}
//
