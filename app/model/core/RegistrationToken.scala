package model.core

import java.time.LocalDateTime
import java.util.UUID

/**
  * Represents token to confirm user registration
  */
final case class RegistrationToken(token: String, userUuid: String, expiresOn: LocalDateTime)
