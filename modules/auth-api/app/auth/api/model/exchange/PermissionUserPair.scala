package auth.api.model.exchange

import auth.core.model.core.Permission

final case class PermissionUserPair(permission: Permission, userUuid: String)

