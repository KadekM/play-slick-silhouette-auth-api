package model.exchange

import auth.model.core.Permission

final case class PermissionUserPair(permission: Permission, userUuid: String)

