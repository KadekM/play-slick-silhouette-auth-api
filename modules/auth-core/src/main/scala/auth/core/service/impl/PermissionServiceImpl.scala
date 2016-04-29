package auth.core.service.impl

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.PermissionToUser
import auth.core.persistence.model.dao.PermissionDao
import auth.core.service.PermissionService

import scala.concurrent.Future

class PermissionServiceImpl(permissionDao: PermissionDao) extends PermissionService {
  override def grant(permission: Permission, userUuid: UUID): Future[Boolean] = permissionDao.grant(permission,userUuid)

  override def revoke(permission: Permission, userUuid: UUID): Future[Boolean] = permissionDao.revoke(permission, userUuid)

  override def allPossible(): Future[Seq[Permission]] = permissionDao.allPossible()

  override def allAssigned(): Future[Seq[PermissionToUser]] = permissionDao.allAssigned()
}
