package auth.persistence.model.authorization.impl

import auth.model.core.{Permission, User}
import auth.persistence.model.authorization.{PermissionAuthorization, PermissionsAuthorizer}
import auth.persistence.model.dao.PermissionDao
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

class DbPermissionsAuthorizerImpl(permissionDao: PermissionDao) extends PermissionsAuthorizer {
  def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
      import scala.concurrent.ExecutionContext.Implicits.global // TODO
      permissionDao.find(required, identity.uuid).map(_.isDefined)
    }
  }
}

class JwtClaimPermissionsAuthorizerImpl(permissionsDao: PermissionDao) extends PermissionsAuthorizer {
  override def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = ???
  }
}
