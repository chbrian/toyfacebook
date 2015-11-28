package facebook

import scala.concurrent.Await


class ProfileActor extends BasicActor {

  import Structures._

  private val profiles = scala.collection.mutable.Map[Int, Profile]()
  private var profileCount = 0

  def createProfile(profile: Profile): Boolean = {
    profiles += (profileCount -> profile)
    profileCount += 1
    true
  }

  def getProfile(profileId: Int): Option[Profile] = {
    if (!profiles.contains(profileId))
      return None
    Some(profiles(profileId))
  }

  def deleteProfile(profileId: Int): Boolean = {
    if (!profiles.contains(profileId))
      return false
    profiles -= profileId
    true
  }

  def receive = {
    case CreateProfile(profile: Profile) =>
      sender ! createProfile(profile)

    case GetProfile(profileId: Int) =>
      sender ! getProfile(profileId)

    case DeleteProfile(profileId: Int) =>
      sender ! deleteProfile(profileId)
  }

}
