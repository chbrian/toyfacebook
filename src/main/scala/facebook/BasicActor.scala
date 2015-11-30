package facebook

import akka.actor.{ActorLogging, Actor}
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by xiaoyong on 11/25/2015.
  */
abstract class BasicActor extends Actor with ActorLogging {


  implicit val timeout = Timeout(10.seconds)
  val userActor = context.actorSelection("/user/userActor")
  val postActor = context.actorSelection("/user/postActor")
  val albumActor = context.actorSelection("/user/albumActor")
  val pictureActor = context.actorSelection("/user/pictureActor")
  val profileActor = context.actorSelection("/user/profileActor")
  val groupActor = context.actorSelection("/user/groupActor")
  val eventActor = context.actorSelection("/user/eventActor")

}
