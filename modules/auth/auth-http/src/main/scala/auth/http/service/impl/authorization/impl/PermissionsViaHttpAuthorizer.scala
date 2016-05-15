package auth.http.service.impl.authorization.impl

import auth.core.model.core.{Permission, PermissionToUser, User}
import auth.core.service.authorization.{PermissionAuthorization, PermissionsAuthorizer}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws.WSClient
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger

/**
  * Authorizes user's permissions via http. Requires authentication endpoint to be on and communicate
  * expected format.
  */
class PermissionsViaHttpAuthorizer(configuration: Configuration, ws: WSClient)(
    implicit ec: ExecutionContext)
    extends PermissionsAuthorizer {
  import auth.core.formatting.core.Rest._

  private val logger   = Logger(this.getClass)
  private val endpoint = configuration.underlying.getString("auth.http.user-service.url")
  private def uri(uuid: String): String = s"$endpoint/users/$uuid/permissions"

  override def require(required: Permission): PermissionAuthorization[JWTAuthenticator] =
    new PermissionAuthorization[JWTAuthenticator] {
      override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(
          implicit request: Request[B]): Future[Boolean] = {
        val endpoint = uri(identity.uuid.toString)

        val f = ws.url(endpoint).get().map { resp ⇒
          val permissions = resp.json.validate[Seq[PermissionToUser]]
          permissions match {
            case JsSuccess(ps, _) ⇒
              ps.map(_.permission).contains(required)
            case JsError(e) ⇒
              logger.error(e.mkString("\n"))
              false
          }
        }
        logError(f, endpoint)
        f
      }
    }

  @inline private def logError[A](f: Future[_], endpoint: String): Unit =
    f.onFailure {
      case e: Throwable ⇒
        logger.error(s"Error during request to $endpoint", e)
    }
}
