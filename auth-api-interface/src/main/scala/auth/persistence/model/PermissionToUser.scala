package auth.persistence.model

import auth.model.core.Permission

final case class PermissionToUser(permission: Permission, userUuid: String)
