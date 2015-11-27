package facebook

class GroupActor extends BasicActor {

  import Structures._

  private val groups = scala.collection.mutable.Map[String, Group]()

  def createGroup(group: Group): Boolean = {
    if (groups.contains(group.id))
      return false
    groups += (group.id -> group)
    true
  }

  def getGroup(groupId: String): Option[Group] = {
    if (!groups.contains(groupId))
      return None
    Some(groups(groupId))
  }

  def deleteGroup(groupId: String): Boolean = {
    if (!groups.contains(groupId))
      return false
    groups -= groupId
    true
  }

  def receive = {
    case CreateGroup(group: Group) =>
      sender ! createGroup(group)

    case GetGroup(groupId: String) =>
      sender ! getGroup(groupId)

    case DeleteGroup(groupId: String) =>
      sender ! deleteGroup(groupId)
  }
}
