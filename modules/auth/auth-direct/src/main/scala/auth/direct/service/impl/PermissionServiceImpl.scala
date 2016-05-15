package auth.direct.service.impl

import java.util.UUID

import auth.core.model.core.{Permission, PermissionToUser}
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import auth.direct.service.PermissionService
import org.postgresql.util.PSQLException

import scala.concurrent.{ExecutionContext, Future}

class PermissionServiceImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends PermissionService with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  val api = new Api()(ec)

  override def grant(permission: Permission, userUuid: UUID): Future[Boolean] =
    db.run(api.Permissions.grant(permission, userUuid)).recoverWith {
      case e: PSQLException ⇒ Future.successful(false)
    }

  override def revoke(permission: Permission, userUuid: UUID): Future[Boolean] =
    db.run(api.Permissions.revoke(permission, userUuid))

  override def allPossible(): Future[Seq[Permission]] =
    db.run(permissionsQuery.result)

  override def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]] =
    db.run(api.Permissions.find(permission, userUuid))

  override def allAssigned(): Future[Seq[PermissionToUser]] =
    db.run(permissionsToUsersQuery.result)

  override def allOfUser(userUuid: UUID): Future[Seq[PermissionToUser]] =
    db.run(api.PermissionsToUsers.allOfUser(userUuid))
}
