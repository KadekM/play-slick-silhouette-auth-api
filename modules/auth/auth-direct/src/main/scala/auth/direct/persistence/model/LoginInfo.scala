package auth.direct.persistence.model

import java.util.UUID

/**
  * Database representation of LoginInfo
  */
final case class LoginInfo(id: Long, userUuid: UUID, providerId: String, providerKey: String)
