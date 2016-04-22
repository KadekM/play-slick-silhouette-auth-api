package persistence.model

import auth.persistence.HasAuthDbProfile
import model.core.UserToken.{TokenAction, UserTokenAction}
import slick.jdbc.JdbcType

trait ModelMappingSupport { self: HasAuthDbProfile ⇒
  import driver.api._

  implicit val userTokenActionMapper: JdbcType[UserTokenAction] = MappedColumnType.base[UserTokenAction, String]({
    case TokenAction.ActivateAccount => "activate_account"
    case TokenAction.ResetPassword => "reset_password"
  }, {
    case "activate_account" => TokenAction.ActivateAccount
    case "reset_password" => TokenAction.ResetPassword
  })
}
