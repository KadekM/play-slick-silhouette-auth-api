package model.core

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import model.core.User.State.Created
import model.core.User.UserState

final case class User(uuid: String,
                      loginInfo: LoginInfo,
                      email: String,
                      firstName: String,
                      lastName: String,

                      state: UserState)
  extends Identity

//todo: active == enumeration

object User {
  def default: User = User(UUID.randomUUID.toString, LoginInfo("",""), "", "", "", state = Created)

  sealed trait UserState
  object State {
    case object Created extends UserState
    case object Activated extends UserState
    case object Deactivated extends UserState
  }
}

