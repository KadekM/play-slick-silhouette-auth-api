package auth.core.persistence.model.repo.impl

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.repo.PermissionRepo
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions, PermissionToUser}
import slick.dbio.Effect.{Read, Write}

import scala.concurrent.ExecutionContext

/**
  * Implementation of permission repo
 *
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class PermissionRepoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends PermissionRepo with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  import driver.profile._

  override def grant(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write] = {
    val act = permissionsToUsersQuery += PermissionToUser(permission, userUuid)
    act.map(changed ⇒ changed != 0)
  }

  override def revoke(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write] = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)
      .delete

    act.map(changed ⇒ changed != 0)
  }

  override def find(permission: Permission, userUuid: UUID): DBIOAction[Option[PermissionToUser], NoStream, Read]  = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)

    act.result.headOption
  }

  override def allPossible: StreamingDriverAction[Seq[Permission], Permission, Read] = permissionsQuery.result

  override def allAssigned: StreamingDriverAction[Seq[PermissionToUser], PermissionToUser, Read] = permissionsToUsersQuery.result
}
