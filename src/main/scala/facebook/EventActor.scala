package facebook

import akka.pattern.ask
import scala.concurrent.Await
import Structures._

/**
  * Created by xiaoyong on 11/27/2015.
  */

class EventActor extends BasicActor {

  private val events = scala.collection.mutable.Map[Int, Event]()
  private var eventCount = 0

  def createEvent(event: Event): Boolean = {
    val future = userActor ? AttendEvent(event.userId, eventCount)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        events += (eventCount -> event)
        eventCount += 1
        true
      case false =>
        false
    }
  }

  def getEvent(eventId: Int): Option[Event] = {
    if (!events.contains(eventId))
      return None
    Some(events(eventId))
  }

  def deleteEvent(eventId: Int): Boolean = {
    if (!events.contains(eventId))
      return false
    val event = events(eventId)
    events -= eventId
    val future = userActor ? CancelEvent(event.userId, eventId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true => true
      case false =>
        log.warning("Event {} can't be removed from user side.", eventId)
        true
    }

  }

  def attendEvent(userId: String, eventId: Int): Boolean = {
    if (!events.contains(eventId))
      return false
    val event = events(eventId)
    val future = userActor ? AttendEvent(userId, eventId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        event.attending += userId
        true
      case false =>
        false
    }
  }

  def cancelEvent(userId: String, eventId: Int): Boolean = {
    if (!events.contains(eventId))
      return false
    val event = events(eventId)
    if (event.attending.contains(userId))
      return false
    val future = userActor ? CancelEvent(userId, eventId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        event.attending -= userId
        true
      case false =>
        false
    }
  }


  def receive = {
    case CreateEvent(event: Event) =>
      sender ! createEvent(event)

    case GetEvent(eventId: Int) =>
      sender ! getEvent(eventId)

    case DeleteEvent(eventId: Int) =>
      sender ! deleteEvent(eventId)

    case AttendEvent(userId: String, eventId: Int) =>
      sender ! attendEvent(userId, eventId)

    case CancelEvent(userId: String, eventId: Int) =>
      sender ! cancelEvent(userId, eventId)


  }
}
