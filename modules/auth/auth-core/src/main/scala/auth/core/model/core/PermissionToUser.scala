package auth.core.model.core

import java.util.UUID

/**
  * Representation of mapping permission to user.
  * May contain values such as expiration time, etc., if required.
  */
final case class PermissionToUser(permission: Permission, userUuid: UUID)
