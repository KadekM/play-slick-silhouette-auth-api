package auth.core.model.core

import java.util.UUID

import auth.core.model.core.User.State.Created
import auth.core.model.core.User.UserState
import com.mohiva.play.silhouette.api.Identity

final case class User(
    uuid: UUID,
    email: String,
    firstName: String,
    lastName: String,
    state: UserState
)
    extends Identity

object User {
  def empty: User = User(UUID.randomUUID, "", "", "", state = Created)

  sealed trait UserState
  object State {

    /**
      * User is created, but does not contain password assigned
      */
    case object Created extends UserState

    /**
      * User has successfully activated account by creating password
      */
    case object Activated extends UserState

    /**
      * User's account has been deactivated
      */
    case object Deactivated extends UserState

    def fromString(x: String): Option[UserState] =
      Array[UserState](Created, Activated, Deactivated).find(_.toString == x)
  }
}
