package auth.api.service.impl

import java.util.UUID
import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.persistence.TablesDefinitions
import auth.api.service.{Hasher, UserTokenService}
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import scala.concurrent.{ExecutionContext, Future}

class UserTokenServiceImpl(
    protected val dbConfigProvider: AuthDatabaseConfigProvider, hasher: Hasher)(
    implicit ec: ExecutionContext)
    extends UserTokenService with AuthDbAccess with TablesDefinitions {
  import driver.api._
  val api = new Api()(ec)

  override def issue(userUuid: UUID, action: UserTokenAction, forHours: Long): Future[UserToken] =
    db.run(api.UserTokens.issue(hasher.hash(UUID.randomUUID.toString), userUuid, action, forHours))

  override def find(token: String): Future[Option[UserToken]] =
    db.run(api.UserTokens.find(token))

  override def consume(token: String): Future[Boolean] =
    db.run(api.UserTokens.remove(token))
}
