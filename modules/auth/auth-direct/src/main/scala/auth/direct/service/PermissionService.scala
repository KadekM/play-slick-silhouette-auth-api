package auth.direct.service

import java.util.UUID

import auth.core.model.core.{Permission, PermissionToUser}

import scala.concurrent.Future

trait PermissionService {

  /**
    * Finds permission - user pair
    */
  def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]]

  /**
    * Grants permission to user
    */
  def grant(permission: Permission, userUuid: UUID): Future[Boolean]

  /**
    * Revokes permission from user
    */
  def revoke(permission: Permission, userUuid: UUID): Future[Boolean]

  /**
    * Lists all permission that user has assigned
    */
  def allOfUser(userUuid: UUID): Future[Seq[PermissionToUser]]

  /**
    * Lists all possible permissions
    */
  def allPossible(): Future[Seq[Permission]]

  /**
    * Lists all permission-user pairs (assigned permissions)
    */
  def allAssigned(): Future[Seq[PermissionToUser]]
}
