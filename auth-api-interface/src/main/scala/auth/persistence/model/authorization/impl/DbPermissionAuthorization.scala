package auth.persistence.model.authorization.impl

import auth.model.core.{Permission, User}
import auth.persistence.model.authorization.PermissionAuthorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

case class WithDbPermission(required: Permission, db: Any) extends PermissionAuthorization[Nothing] {
  override def isAuthorized[B](identity: User, authenticator: Nothing)(implicit request: Request[B]): Future[Boolean] = {
    ???
  }
}

case class WithJwtClaim(required: Permission) extends PermissionAuthorization[JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = ???
}
