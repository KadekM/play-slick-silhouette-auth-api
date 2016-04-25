package auth.persistence.model

import auth.model.core.Permission

final case class PermissionToModel(permission: Permission, userUuid: String)
