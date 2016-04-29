package auth.api.model.exchange

import java.util.UUID

import auth.core.model.core.Permission

final case class PermissionUserPair(permission: Permission, userUuid: UUID)

