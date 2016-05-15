package auth.api.controllers

import auth.api.model.exchange._
import auth.api.service.AuthService
import auth.api.service.AuthService._
import auth.core.DefaultEnv
import auth.core.util.CookieSettings
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/-}

/**
  * Sign in using login/password credentials (no 3d party social login).
  */
class SignInCredentialsController @Inject()(silhouette: Silhouette[DefaultEnv],
                                            authCookieSettings: CookieSettings,
                                            authService: AuthService)(
    implicit ec: ExecutionContext)
    extends Controller with ResponseHelpers {

  import auth.api.formatting.exchange.Rest._

  def signIn: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body
      .validate[SignIn]
      .map { signIn ⇒
        authService.signIn(signIn.identifier, signIn.password).flatMap {
          case -\/(UserNotActivated) ⇒
            Future.successful(Conflict(Json.toJson(Bad("signin.state.not.activated"))))

          case -\/(LoginInfoMissing) ⇒
            Future.successful(Unauthorized(Json.toJson(Bad("login.info.missing"))))

          case -\/(InvalidCredentials) ⇒
            Future.successful(Unauthorized(Json.toJson(Bad("invalid.credentials"))))

          case -\/(ProviderError(e)) ⇒
            Future.successful(Unauthorized(Json.toJson(Bad("provider.error"))))

          case \/-(SignedIn(token)) ⇒
            val response = Ok(Json.toJson(token))
            val auth     = silhouette.env.authenticatorService.embed(token.token, response)
            auth.map { authResult ⇒
              if (signIn.rememberMe) authResult.withCookies(authCookieSettings.make(token.token))
              else authResult
            }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }
}
