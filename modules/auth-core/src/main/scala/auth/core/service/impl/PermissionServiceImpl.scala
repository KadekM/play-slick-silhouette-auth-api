package auth.core.service.impl

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.repo.PermissionRepo
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, PermissionToUser}
import auth.core.service.PermissionService

import scala.concurrent.Future

class PermissionServiceImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider,
                            permissionRepo: PermissionRepo) extends PermissionService with AuthDbAccess {

  override def grant(permission: Permission, userUuid: UUID): Future[Boolean] =
    db.run(permissionRepo.grant(permission,userUuid))

  override def revoke(permission: Permission, userUuid: UUID): Future[Boolean] =
    db.run(permissionRepo.revoke(permission, userUuid))

  override def allPossible(): Future[Seq[Permission]] =
    db.run(permissionRepo.allPossible)

  override def allAssigned(): Future[Seq[PermissionToUser]] =
    db.run(permissionRepo.allAssigned)

  override def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]] =
    db.run(permissionRepo.find(permission, userUuid))
}
