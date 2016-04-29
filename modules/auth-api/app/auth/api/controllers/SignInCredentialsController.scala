package auth.api.controllers

import auth.api.model.exchange._
import auth.api.utils.date.Conversions
import auth.core.DefaultEnv
import auth.core.model.core.User
import auth.core.service.UserService
import auth.core.utils.CookieSettings
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{ LoginInfo, Silhouette }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._

import scala.concurrent.Future

/**
  * Sign in using login/password credentials (no 3d party social login).
  */
class SignInCredentialsController @Inject() (silhouette: Silhouette[DefaultEnv],
    authCookieSettings: CookieSettings,
    userService: UserService,
    credentialsProvider: CredentialsProvider) extends Controller with ResponseHelpers {

  import play.api.libs.concurrent.Execution.Implicits._
  import auth.api.formatting.exchange.rest._

  def signIn: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[Credentials].map { credentials ⇒
      val f = credentialsProvider.authenticate(credentials)
        .flatMap { loginInfo ⇒

          userService.retrieve(loginInfo).flatMap {
            case Some(user) ⇒

              user.state match {
                case User.State.Activated ⇒
                  runSignIn(loginInfo)

                case User.State.Created | User.State.Deactivated ⇒
                  Future.successful(BadRequest(Json.toJson(Bad("signin.state.not.activated"))))
              }

            case None ⇒ Future.successful(Unauthorized(Json.toJson(Bad.empty)))
          }
        }

      f.recoverWith {
        case e: ProviderException ⇒
          //TODO: logging
          Future.successful(Unauthorized(Json.toJson(Bad.empty)))
      }
    }.recoverTotal(badRequestWithMessage)
  }

  /**
    * Returns Token response with encoded auth data
    */
  private def runSignIn(loginInfo: LoginInfo)(implicit request: Request[JsValue]): Future[Result] =
    for {
      authenticator ← silhouette.env.authenticatorService.create(loginInfo)
      tokenValue ← silhouette.env.authenticatorService.init(authenticator)

      expiration = Conversions.jodaToJava(authenticator.expirationDateTime)

      response = Ok(Json.toJson(Token(token = tokenValue, expiresOn = expiration)))
      authResult ← silhouette.env.authenticatorService.embed(tokenValue, response)
    } yield authResult.withCookies(authCookieSettings.make(tokenValue))
}
