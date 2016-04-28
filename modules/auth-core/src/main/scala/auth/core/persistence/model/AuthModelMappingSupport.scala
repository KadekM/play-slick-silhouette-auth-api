package auth.core.persistence.model

import auth.core.model.core.{AccessAdmin, AccessBar, Permission, User}
import User.State._
import User._
import auth.core.persistence.HasAuthDbProfile
import slick.jdbc.JdbcType

trait AuthModelMappingSupport { self: HasAuthDbProfile =>
  import driver.api._

  // TODO: sort out duplication
  implicit val stateMapper: JdbcType[UserState] = MappedColumnType.base[UserState, String]({
    case Created     ⇒ "created"
    case Activated   ⇒ "activated"
    case Deactivated ⇒ "deactivated"
  }, {
    case "created"     ⇒ Created
    case "activated"   ⇒ Activated
    case "deactivated" ⇒ Deactivated
  })

  implicit val permissionMapper: JdbcType[Permission] = MappedColumnType.base[Permission, String]({
    case AccessAdmin => Permission.accessAdmin
    case AccessBar => Permission.accessBar
  }, {
    case Permission.accessAdmin => AccessAdmin
    case Permission.accessBar => AccessBar
  })
}
