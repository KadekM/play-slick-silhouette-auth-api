package service.impl

import java.time.LocalDateTime
import java.util.UUID

import com.google.inject.Inject
import model.core.UserToken
import model.core.UserToken.UserTokenAction
import service.{ Hasher, UserTokenService }

import scala.concurrent.Future
import scala.collection.mutable.ArrayBuffer

// TODO: persistence.mapping.dao-s - should not access directly db
class UserTokenServiceImpl extends UserTokenService {
  override def issue(userUuid: String, action: UserTokenAction): Future[UserToken] = ???

  override def claim(token: String): Future[Option[UserToken]] = ???
}

/**
  * Not thread safe. Usage in production is discouraged (as it needs to be singleton).
  */
class InMemoryUserTokenServiceImpl @Inject() (hasher: Hasher) extends UserTokenService {

  val tokens: ArrayBuffer[UserToken] = ArrayBuffer.empty[UserToken]

  override def issue(userUuid: String, action: UserTokenAction): Future[UserToken] = {
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
