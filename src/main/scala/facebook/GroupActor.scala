package facebook

import akka.pattern.ask
import scala.concurrent.Await
import Structures._

/**
  * Created by xiaoyong on 11/26/2015.
  */

class GroupActor extends BasicActor {

  private val groups = scala.collection.mutable.Map[String, Group]()

  def createGroup(group: Group): Boolean = {
    if (groups.contains(group.id))
      return false
    val future = userActor ? JoinUserGroup(group.userId, group.id)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        groups += (group.id -> group)
        true
      case false =>
        false
    }
  }

  def getGroup(groupId: String): Option[Group] = {
    if (!groups.contains(groupId))
      return None
    Some(groups(groupId))
  }

  def deleteGroup(groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false

    val group = groups(groupId)
    for (member <- group.members) {
      val future = userActor ? LeaveUserGroup(member, group.id)
      Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
        case true =>
        case false =>
          log.warning("Group {} can't be removed from user side", groupId)
      }
    }
    groups -= groupId
    true
  }

  def joinUserGroup(userId: String, groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false
    val group = groups(groupId)
    val future = userActor ? LeaveUserGroup(userId, groupId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        group.members += userId
        true
      case false =>
        false
    }
  }

  def leaveUserGroup(userId: String, groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false
    val group = groups(groupId)
    if (!group.members.contains(userId))
      return false
    val future = userActor ? LeaveUserGroup(userId, groupId)
    Await.result(future, timeout.duration).asInstanceOf[Boolean] match {
      case true =>
        group.members -= userId
        true
      case false =>
        false
    }
  }

  def addEventGroup(eventId: Int, groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false
    val group = groups(groupId)
    if (group.events.contains(eventId)) {
      log.warning("Event {} already in the group", eventId)
      true
    }
    else {
      val future = eventActor ? GetEvent(eventId)
      Await.result(future, timeout.duration).asInstanceOf[Option[Event]] match {
        case None =>
          log.error("Event {} doesn't exist", eventId)
          false
        case Some(event: Event) =>
          group.events += eventId
          true
      }
    }
  }

  def removeEventGroup(eventId: Int, groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false
    val group = groups(groupId)
    if (!group.events.contains(eventId)) {
      return false
    }
    group.events -= eventId
    true
  }

  def receive = {
    case CreateGroup(group: Group) =>
      sender ! createGroup(group)

    case GetGroup(groupId: String) =>
      sender ! getGroup(groupId)

    case DeleteGroup(groupId: String) =>
      sender ! deleteGroup(groupId)

    case JoinUserGroup(userId: String, groupId: String) =>
      sender ! joinUserGroup(userId, groupId)

    case LeaveUserGroup(userId: String, groupId: String) =>
      sender ! leaveUserGroup(userId, groupId)

    case AddEventGroup(eventId: Int, groupId: String) =>
      sender ! addEventGroup(eventId, groupId)

    case RemoveEventGroup(eventId: Int, groupId: String) =>
      sender ! removeEventGroup(eventId, groupId)
  }
}
