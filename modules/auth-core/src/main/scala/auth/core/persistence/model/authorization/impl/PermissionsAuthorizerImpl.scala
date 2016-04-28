package auth.core.persistence.model.authorization.impl

import auth.core.model.core.{ Permission, User }
import auth.core.persistence.model.authorization.{ PermissionAuthorization, PermissionsAuthorizer }
import auth.core.persistence.model.dao.PermissionDao
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

/**
  * Authorizer that hits database (potentially cache) for each permission request
  */
class DbPermissionsAuthorizerImpl(permissionDao: PermissionDao) extends PermissionsAuthorizer {
  def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
      import scala.concurrent.ExecutionContext.Implicits.global // TODO
      permissionDao.find(required, identity.uuid).map(_.isDefined)
    }
  }
}

/**
  * Authorizer that tries to read permission from claims object inside the JWT token
  */
class JwtClaimPermissionsAuthorizerImpl(permissionsDao: PermissionDao) extends PermissionsAuthorizer {
  override def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = ???
  }
}
