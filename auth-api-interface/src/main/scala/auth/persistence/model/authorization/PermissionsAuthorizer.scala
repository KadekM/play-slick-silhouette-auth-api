package auth.persistence.model.authorization

import auth.model.core.Permission
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

trait PermissionsAuthorizer {
  def require(required: Permission): PermissionAuthorization[JWTAuthenticator]
}
