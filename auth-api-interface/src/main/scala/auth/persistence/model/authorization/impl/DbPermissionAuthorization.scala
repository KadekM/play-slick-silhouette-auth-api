package auth.persistence.model.authorization.impl

import auth.model.core.{Permission, User}
import auth.persistence.model.authorization.PermissionAuthorization
import auth.persistence.model.dao.PermissionDao
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

  // TODO: jwt authenticator
case class WithDbPermission(required: Permission, permissionDao: PermissionDao) extends PermissionAuthorization[JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
    import scala.concurrent.ExecutionContext.Implicits.global // TODO
    permissionDao.find(required, identity.uuid).map(_.isDefined)
  }
}

case class WithJwtClaim(required: Permission) extends PermissionAuthorization[JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = ???
}
