package auth.core.service

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.PermissionToUser

import scala.concurrent.Future

trait PermissionService {
  def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]]

  def grant(permission: Permission, userUuid: UUID): Future[Boolean]

  def revoke(permission: Permission, userUuid: UUID): Future[Boolean]

  def allPossible(): Future[Seq[Permission]]

  def allAssigned(): Future[Seq[PermissionToUser]]
}
