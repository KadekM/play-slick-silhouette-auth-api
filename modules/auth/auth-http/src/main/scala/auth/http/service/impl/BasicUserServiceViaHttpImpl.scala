package auth.http.service.impl

import auth.core.model.core.User
import auth.core.service.BasicUserService
import com.mohiva.play.silhouette.api.LoginInfo
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger

/**
  * Basic user service via http implementation. Requires authentication endpoint to be on and communicate
  * expected format.
  */
class BasicUserServiceViaHttpImpl(configuration: Configuration, ws: WSClient)(
    implicit ec: ExecutionContext)
    extends BasicUserService {
  import auth.core.formatting.core.Rest._

  private val logger   = Logger(this.getClass)
  private val endpoint = configuration.underlying.getString("auth.http.user-service.url")
  private def uri(id: String, key: String): String = s"$endpoint/users/login/$id/$key"
  private def encode(x: String): String =
    java.net.URLEncoder.encode(x, "UTF-8")

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    // Encode providerKey, since it may be wild, like email address
    val endpoint = uri(loginInfo.providerID, encode(loginInfo.providerKey))

    val f = ws.url(endpoint).get().map { resp ⇒
      val user = resp.json.validate[User]

      user match {
        case JsSuccess(u, _) ⇒ Some(u)
        case JsError(e) ⇒
          logger.error(e.mkString("\n"))
          None
      }
    }
    logError(f, endpoint)
    f
  }

  @inline private def logError[A](f: Future[_], endpoint: String): Unit =
    f.onFailure {
      case e: Throwable ⇒
        logger.error(s"Error during request to $endpoint", e)
    }
}
