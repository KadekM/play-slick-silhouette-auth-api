package auth.persistence.model

import auth.model.core.User.State._
import auth.model.core.User._
import auth.persistence.HasAuthDbProfile
import slick.jdbc.JdbcType

trait AuthModelMappingSupport { self: HasAuthDbProfile =>
  import driver.api._

  implicit val stateMapper: JdbcType[UserState] = MappedColumnType.base[UserState, String]({
    case Created     ⇒ "created"
    case Activated   ⇒ "activated"
    case Deactivated ⇒ "deactivated"
  }, {
    case "created"     ⇒ Created
    case "activated"   ⇒ Activated
    case "deactivated" ⇒ Deactivated
  })
}
