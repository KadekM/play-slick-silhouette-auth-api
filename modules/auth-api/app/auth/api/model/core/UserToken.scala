package auth.api.model.core

import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken.UserTokenAction

/**
  * Represents token with actions
  */
final case class UserToken(token: String,
  userUuid: UUID,
  expiresOn: LocalDateTime,
  tokenAction: UserTokenAction)

object UserToken {
  sealed trait UserTokenAction
  object TokenAction {
    case object ActivateAccount extends UserTokenAction
    case object ResetPassword extends UserTokenAction

    def fromString(x: String): Option[UserTokenAction] =
      Array(ActivateAccount, ResetPassword).find(_.toString == x)
  }
}
