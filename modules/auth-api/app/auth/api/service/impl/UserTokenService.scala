package auth.api.service.impl

import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.persistence.repo.{ Hasher, UserTokenRepo }
import auth.api.service.UserTokenService
import auth.core.persistence.model.{ AuthDatabaseConfigProvider, AuthDbAccess }

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContext, Future }

class UserTokenServiceImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider,
    userTokenRepo: UserTokenRepo)(implicit ec: ExecutionContext) extends UserTokenService with AuthDbAccess {

  import driver.api._

  override def issue(userUuid: UUID, action: UserTokenAction, forHours: Long): Future[UserToken] =
    db.run(userTokenRepo.issue(userUuid, action, forHours))

  override def claim(token: String): Future[Option[UserToken]] = {
    val act = for {
      t ← userTokenRepo.find(token)
      _ ← userTokenRepo.remove(token)
    } yield t

    db.run(act.transactionally)
  }
}

/**
  * Not thread safe. Usage in production is discouraged (and it needs to be singleton).
  */
class InMemoryUserTokenServiceImpl(hasher: Hasher) extends UserTokenService {

  val tokens: ArrayBuffer[UserToken] = ArrayBuffer.empty[UserToken]

  override def issue(userUuid: UUID, action: UserTokenAction, forHours: Long): Future[UserToken] = {
    val tokenHash = hasher.hash(UUID.randomUUID.toString)

    // TODO: expiration days to config
    val t = UserToken(tokenHash, userUuid, LocalDateTime.now.plusHours(forHours), action)
    tokens += t
    Future.successful(t)
  }

  override def claim(token: String): Future[Option[UserToken]] = {
    val t = tokens.find(x ⇒ x.token == token && x.expiresOn.isAfter(LocalDateTime.now))
    t.map(found ⇒ tokens -= found)
    Future.successful(t)
  }
}
