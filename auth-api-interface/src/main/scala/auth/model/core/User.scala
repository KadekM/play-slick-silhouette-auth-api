package auth.model.core

import java.util.UUID

import auth.model.core.User.State.Created
import auth.model.core.User.UserState
import com.mohiva.play.silhouette.api.Identity

final case class User(uuid: String,
                      email: String,
                      firstName: String,
                      lastName: String,

                      state: UserState)
  extends Identity

object User {
  def empty: User = User(UUID.randomUUID.toString, "", "", "", state = Created)

  sealed trait UserState
  object State {
    case object Created extends UserState
    case object Activated extends UserState
    case object Deactivated extends UserState
  }
}

