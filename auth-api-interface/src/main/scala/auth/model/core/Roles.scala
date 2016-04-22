package auth.model.core

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Request

import scala.concurrent.Future

sealed trait Permission

case object Admin extends Permission
case object SpringBarAccess extends Permission


case class WithPermission(required: Permission) extends Authorization[User, JWTAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator )(implicit request: Request[B]): Future[Boolean] = {
    Future.successful { false }
  }
}