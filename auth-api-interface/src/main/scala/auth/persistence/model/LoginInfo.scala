package auth.persistence.model

final case class LoginInfo(id: Long, userUuid: String, providerId: String, providerKey: String)
