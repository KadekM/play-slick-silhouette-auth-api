package auth.core.service.authorization.impl

import auth.core.model.core.{Permission, User}
import auth.core.persistence.model.repo.PermissionRepo
import auth.core.service.authorization.{PermissionAuthorization, PermissionsAuthorizer}
import auth.core.service.PermissionService
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}

/**
  * Authorizer that hits database (potentially cache) for each permission request
  */
class DbPermissionsAuthorizerImpl(permissionService: PermissionService)(implicit ec: ExecutionContext) extends PermissionsAuthorizer {
  def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
      permissionService.find(required, identity.uuid).map(_.isDefined)
    }
  }
}

/**
  * Authorizer that tries to read permission from claims object inside the JWT token
  */
class JwtClaimPermissionsAuthorizerImpl()(implicit ec: ExecutionContext) extends PermissionsAuthorizer {
  override def require(required: Permission): PermissionAuthorization[JWTAuthenticator] = new PermissionAuthorization[JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = ???
  }
}
