package auth.api.model.dao.impl

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.model.TablesDefinitions
import auth.api.model.dao.{Hasher, UserTokenDao}

import scala.concurrent.Future

//TODO
import scala.concurrent.ExecutionContext.Implicits.global

class UserTokenDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider,
                       hasher: Hasher)
  extends UserTokenDao with AuthDbAccess with TablesDefinitions {
  import driver.api._

  override def issue(userUuid: String, action: UserTokenAction): Future[UserToken] = {
    val tokenHash = hasher.hash(UUID.randomUUID.toString)
    // TODO: expiration days to config
    val token = UserToken(tokenHash, userUuid, LocalDateTime.now.plusDays(1), action)
    val act = userTokensQuery += token
    db.run(act).map(_ => token)
  }

  override def find(token: String): Future[Option[UserToken]] = {
    val act = userTokensQuery
      .filter(_.token === token)

    db.run(act.result.headOption)
  }

  override def remove(token: String): Future[Boolean] = {
    val act = userTokensQuery
      .filter(_.token === token)

    db.run(act.delete).map(deleted => deleted > 0)
  }
}
