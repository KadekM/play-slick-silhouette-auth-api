package auth.core.persistence.model

import auth.core.model.core.Permission

final case class PermissionToUser(permission: Permission, userUuid: String)
