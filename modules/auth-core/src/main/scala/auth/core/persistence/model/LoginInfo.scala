package auth.core.persistence.model

import java.util.UUID

final case class LoginInfo(id: Long, userUuid: UUID, providerId: String, providerKey: String)
