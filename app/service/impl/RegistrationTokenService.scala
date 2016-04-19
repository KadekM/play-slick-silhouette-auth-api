package service.impl

import java.time.LocalDateTime
import java.util.UUID

import com.google.inject.Inject
import model.core.RegistrationToken
import service.{Hasher, RegistrationTokenService}

import scala.concurrent.Future
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

// TODO: dao-s - should not access directly db
class RegistrationTokenServiceImpl extends RegistrationTokenService {
  override def issue(userUuid: String): Future[RegistrationToken] = ???

  override def claim(token: String): Future[Option[RegistrationToken]] = ???
}

/**
  * Not thread safe. Usage in production is discouraged (as it needs to be singleton).
  */
class InMemoryRegistrationTokenServiceImpl @Inject() (hasher: Hasher) extends RegistrationTokenService {

  val tokens: ArrayBuffer[RegistrationToken] = ArrayBuffer.empty[RegistrationToken]

  override def issue(userUuid: String): Future[RegistrationToken] = {
    val tokenHash = hasher.hash(UUID.randomUUID.toString)

    // TODO: expiration days to config
    val t = RegistrationToken(tokenHash, userUuid, LocalDateTime.now.plusDays(1))
    tokens += t
    Future.successful(t)
  }

  override def claim(token: String): Future[Option[RegistrationToken]] = {
    val t = tokens.find(x => x.token == token)
    Future.successful(t)
  }
}
