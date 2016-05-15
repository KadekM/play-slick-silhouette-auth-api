package auth.direct.persistence.model

import auth.core.model.core.Permission
import auth.core.model.core.User._
import auth.direct.persistence.HasAuthDbProfile
import slick.jdbc.JdbcType

/**
  * Provides support for mapping over basic auth types
  */
trait AuthModelMappingSupport { self: HasAuthDbProfile ⇒
  import driver.api._

  implicit val userStateMapper: JdbcType[UserState] =
    MappedColumnType.base[UserState, String](_.toString, State.fromString(_).get)

  implicit val permissionMapper: JdbcType[Permission] =
    MappedColumnType.base[Permission, String](_.toString, Permission.fromString(_).get)
}
