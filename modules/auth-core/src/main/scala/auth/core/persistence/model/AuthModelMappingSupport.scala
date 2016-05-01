package auth.core.persistence.model

import auth.core.model.core.{AccessAdmin, AccessBar, Permission, User}
import User.State._
import User._
import auth.core.persistence.HasAuthDbProfile
import slick.jdbc.JdbcType

trait AuthModelMappingSupport { self: HasAuthDbProfile =>
  import driver.api._

  implicit val stateMapper: JdbcType[UserState] =
    MappedColumnType.base[UserState, String](_.toString, State.fromString(_).get)

  implicit val permissionMapper: JdbcType[Permission] =
    MappedColumnType.base[Permission, String](_.toString, Permission.fromString(_).get)
}
