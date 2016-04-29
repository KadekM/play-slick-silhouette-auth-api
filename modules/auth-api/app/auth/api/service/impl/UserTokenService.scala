package auth.api.service.impl

import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.model.dao.{Hasher, UserTokenDao}
import auth.api.service.UserTokenService

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

class UserTokenServiceImpl(userTokenDao: UserTokenDao) extends UserTokenService {
  override def issue(userUuid: UUID, action: UserTokenAction): Future[UserToken] =
    userTokenDao.issue(userUuid, action)

  override def claim(token: String): Future[Option[UserToken]] = {
    val t = userTokenDao.find(token)
    userTokenDao.remove(token)
    t
  }
}

/**
  * Not thread safe. Usage in production is discouraged (and it needs to be singleton).
  */
class InMemoryUserTokenServiceImpl(hasher: Hasher) extends UserTokenService {

  val tokens: ArrayBuffer[UserToken] = ArrayBuffer.empty[UserToken]

  override def issue(userUuid: UUID, action: UserTokenAction): Future[UserToken] = {
    val tokenHash = hasher.hash(UUID.randomUUID.toString)

    // TODO: expiration days to config
    val t = UserToken(tokenHash, userUuid, LocalDateTime.now.plusDays(1), action)
    tokens += t
    Future.successful(t)
  }

  override def claim(token: String): Future[Option[UserToken]] = {
    val t = tokens.find(x ⇒ x.token == token)
    t.map(found ⇒ tokens -= found)
    Future.successful(t)
  }
}
