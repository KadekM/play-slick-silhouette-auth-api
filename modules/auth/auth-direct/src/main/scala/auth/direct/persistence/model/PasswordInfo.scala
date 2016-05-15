package auth.direct.persistence.model

/**
  * Database representation of password info
  */
final case class PasswordInfo(
    loginInfoId: Long,
    hasher: String,
    password: String,
    salt: Option[String]
)
