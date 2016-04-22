package auth.persistence.model

final case class PasswordInfo(loginInfoId: Long,
                              hasher: String,
                              password: String,
                              salt: Option[String])
