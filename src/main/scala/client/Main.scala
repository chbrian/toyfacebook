//package client
//
//import akka.actor.ActorSystem
//import akka.event.Logging
//
//import scala.util.{Failure, Success}
//
///**
//  * Client App for benchmarking.
//  * Created by alan on 11/24/2015.
//  */
//object Main extends App with Requests {
//
//  // Create an ActorSystem to host our client application in
//  implicit val system = ActorSystem("client")
//
//  //Ensure that the constructed ActorSystem is shut down when the JVM shuts down
//  sys.addShutdownHook(system.shutdown())
//
//  import system.dispatcher
//
//  val log = Logging(system, getClass)
//
//  val host = "http://localhost:8080/"
//
//  val id = "yi"
//
//  // !!! All results are futures, they may return in random order! Careful
//  val result0 = createUser(host, id, id, "abc")
//  result0 onComplete {
//    case Success(response) => log.info("Create user {}, received response: {}", id, response.status)
//    case Failure(error) => log.warning("Create user {} request error: {}", id, error.getMessage)
//  }
//
//  Thread.sleep(1000)
//  // TODO: use actor or future composition instead of sleep (blocking!)
//  val result1 = getUser(host, id)
//  result1 onComplete {
//    case Success(response) => log.info("Get user {}, received response: {}", id, response)
//    case Failure(error) => log.warning("Get user {} request error: {}", id, error.getMessage)
//  }
//
//  Thread.sleep(1000)
//  // TODO: use actor or future composition instead of sleep (blocking!)
//  val result3 = deleteUser(host, id)
//  result3 onComplete {
//    case Success(response) => log.info("Delete user {}, received response: {}", id, response)
//    case Failure(error) => log.warning("Delete user {} request error: {}", id, error.getMessage)
//  }
//
//  result3 onComplete { _ => system.shutdown() }
//
//}
