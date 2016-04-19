package persistence.drivers

import model.core.User.State._
import model.core.User.UserState
import slick.jdbc.JdbcType

// TODO: extract capabilities so we dont need to depend on postrgres just because of mappings
trait AuthPostgresDriver extends PostgresDriver {
  override val api: AuthPostgresDriverApi.type = AuthPostgresDriverApi

  object AuthPostgresDriverApi extends PostgresDriverApi {
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
}

