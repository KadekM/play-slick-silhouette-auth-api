package model.exchange

import java.time.LocalDateTime

/**
  * Represents token that is returned to the user
  */
final case class Token(token: String, expiresOn: LocalDateTime)
