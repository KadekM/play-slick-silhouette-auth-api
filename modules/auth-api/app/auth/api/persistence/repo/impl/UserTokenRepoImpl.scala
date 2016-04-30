package auth.api.persistence.repo.impl

import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.persistence.TablesDefinitions
import auth.api.persistence.repo.{Hasher, UserTokenRepo}
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import slick.dbio.Effect.{Read, Write}

import scala.concurrent.ExecutionContext

/**
  * Implementation of user token repo
  *
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class UserTokenRepoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider,
  hasher: Hasher)(implicit ec: ExecutionContext)
    extends UserTokenRepo with AuthDbAccess with TablesDefinitions {
  import driver.api._

  override def issue(userUuid: UUID, action: UserTokenAction): DBIOAction[UserToken, NoStream, Write] = {
    val tokenHash = hasher.hash(UUID.randomUUID.toString)
    // TODO: expiration days to config/argument
    val token = UserToken(tokenHash, userUuid, LocalDateTime.now.plusDays(1), action)
    val act = userTokensQuery += token
    act.map(_ ⇒ token)
  }

  override def find(token: String): DBIOAction[Option[UserToken], NoStream, Read] =
    userTokensQuery
      .filter(_.token === token)
      .result.headOption

  override def remove(token: String): DBIOAction[Boolean, NoStream, Write] =
    userTokensQuery
      .filter(_.token === token)
      .delete.map(deleted ⇒ deleted > 0)
}
