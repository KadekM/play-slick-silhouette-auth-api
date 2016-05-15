package auth.direct.service

import java.util.UUID

import auth.core.model.core.User
import auth.core.service.BasicUserService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import scala.concurrent.Future

/**
  * Handles actions to users
  */
trait UserService extends BasicUserService {

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User]

  /**
    * Sets new state to user with `userUuid`
    * @return true if new state was set successfuly, otherwise false
    */
  def setState(userUuid: UUID, newState: User.UserState): Future[Boolean]

  /**
    * Retrieves the user
    * @return Some of user if found, otherwise None
    */
  def retrieve(userUuid: UUID): Future[Option[User]]

  /**
    * Lists all users
    */
  def list(): Future[Seq[User]]
}
