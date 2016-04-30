package auth.core.service.authorization

import auth.core.model.core.Permission
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

/**
  * Returns authorizer for required permission. Authorizers are used by Silhouette,
  * to determine whether or not is access allowed.
  */
trait PermissionsAuthorizer {
  def require(required: Permission): PermissionAuthorization[JWTAuthenticator]
}
