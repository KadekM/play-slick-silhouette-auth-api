package auth.api.persistence

import auth.api.model.core.UserToken.{TokenAction, UserTokenAction}
import auth.core.persistence.HasAuthDbProfile
import slick.jdbc.JdbcType

/**
  * Provides support for mapping model to db
  */
trait ModelMappingSupport { self: HasAuthDbProfile ⇒
  import driver.api._

  implicit val userTokenActionMapper: JdbcType[UserTokenAction] =
    MappedColumnType.base[UserTokenAction, String](_.toString, TokenAction.fromString(_).get)
}
