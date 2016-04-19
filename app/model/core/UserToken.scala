package model.core

import java.time.LocalDateTime

import model.core.UserToken.UserTokenAction

/**
  * Represents token with actions
  */
final case class UserToken(token: String, userUuid: String, expiresOn: LocalDateTime, tokenAction: UserTokenAction)

object UserToken {
  sealed trait UserTokenAction
  object TokenAction {
    case object ActivateAccount extends UserTokenAction
    case object ResetPassword extends UserTokenAction
  }
}
