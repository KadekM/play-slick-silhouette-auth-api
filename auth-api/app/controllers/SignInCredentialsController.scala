package controllers

import auth.DefaultEnv
import auth.model.core.User
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.exchange.{Bad, Token}
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller, Request}
import auth.service.UserService

import scala.concurrent.Future

/**
  * Sign in using login/password credentials (no 3d party social login).
  */
class SignInCredentialsController(silhouette: Silhouette[DefaultEnv],
    translate: MessagesApi,
    userService: UserService,
    credentialsProvider: CredentialsProvider) extends Controller with ResponseHelpers {

  import formatting.exchange.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  def signIn: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[Credentials].map { credentials ⇒
      credentialsProvider.authenticate(credentials).flatMap { loginInfo ⇒

        userService.retrieve(loginInfo).flatMap {
          case Some(user) ⇒

            user.state match {
              case User.State.Activated ⇒
                runSignIn(loginInfo)

              case User.State.Created ⇒
                Future.successful(BadRequest(Json.toJson(Bad(message = translate("signin.state.not.activated")))))

              case User.State.Deactivated ⇒
                Future.successful(BadRequest(Json.toJson(Bad(message = translate("signin.state.not.activated")))))
            }

          case None ⇒ Future.failed(new Exception("todo: couldn't find user"))
        }
      }
    }.recoverTotal(badRequestWithMessage)
  }

  /**
    * Returns Token response with encoded auth data
    */
  private def runSignIn(loginInfo: LoginInfo)(implicit request: Request[JsValue]): Future[AuthenticatorResult] =
    for {
      authenticator ← silhouette.env.authenticatorService.create(loginInfo)
      value ← silhouette.env.authenticatorService.init(authenticator)

      expiration = utils.date.Conversions.jodaToJava(authenticator.expirationDateTime)

      response = Ok(Json.toJson(Token(token = value, expiresOn = expiration)))
      authResult ← silhouette.env.authenticatorService.embed(value, response)
    } yield authResult
}
