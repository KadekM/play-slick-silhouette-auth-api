package auth.service

import auth.model.core.Permission
import auth.persistence.model.PermissionToUser

import scala.concurrent.Future

trait PermissionService {

  def grant(permission: Permission, userUuid: String): Future[Boolean]

  def revoke(permission: Permission, userUuid: String): Future[Boolean]

  def allPossible(): Future[Seq[Permission]]

  def allAssigned(): Future[Seq[PermissionToUser]]
}
