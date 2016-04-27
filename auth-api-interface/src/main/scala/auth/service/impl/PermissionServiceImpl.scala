package auth.service.impl

import auth.model.core.Permission
import auth.persistence.model.PermissionToUser
import auth.persistence.model.dao.PermissionDao
import auth.service.PermissionService

import scala.concurrent.Future

class PermissionServiceImpl(permissionDao: PermissionDao) extends PermissionService {
  override def grant(permission: Permission, userUuid: String): Future[Boolean] = permissionDao.grant(permission,userUuid)

  override def revoke(permission: Permission, userUuid: String): Future[Boolean] = permissionDao.revoke(permission, userUuid)

  override def allPossible(): Future[Seq[Permission]] = permissionDao.allPossible()

  override def allAssigned(): Future[Seq[PermissionToUser]] = permissionDao.allAssigned()
}
