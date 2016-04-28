package auth.api.model.core

import java.sql.Timestamp
import java.time.LocalDateTime

import auth.api.model.core.UserToken.UserTokenAction

/**
  * Represents token with actions
  */
final case class UserToken(token: String,
                           userUuid: String,
                           expiresOn: LocalDateTime,
                           tokenAction: UserTokenAction)

object UserToken {
  // TODO: extract tokenAction
  sealed trait UserTokenAction
  object TokenAction {
    // TODO: nicer way to se/deserialize
    case object ActivateAccount extends UserTokenAction
    case object ResetPassword extends UserTokenAction

    val activateAccount = "activate_account"
    val resetPassword = "reset_password"
  }
}
