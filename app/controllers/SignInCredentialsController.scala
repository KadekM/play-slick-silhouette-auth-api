package controllers

import java.time.LocalDateTime

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.exchange.{Bad, Token}
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller}
import service.UserService
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * Sign in using login/password credentials (no 3d party social login).
  */
class SignInCredentialsController @Inject() (silhouette: Silhouette[DefaultEnv],
    messagesApi: MessagesApi,
    userService: UserService,
    credentialsProvider: CredentialsProvider) extends Controller with ResponseHelpers {

  import model.exchange.format.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  def signIn: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[Credentials].map { credentials ⇒
      credentialsProvider.authenticate(credentials).flatMap { loginInfo ⇒
        userService.retrieve(loginInfo).flatMap {
          case Some(user) ⇒
            for {
              authenticator ← silhouette.env.authenticatorService.create(loginInfo)
              value ← silhouette.env.authenticatorService.init(authenticator)

              expiration = utils.date.Conversions.jodaToJava(authenticator.expirationDateTime)

              response = Ok(Json.toJson(Token(token = value, expiresOn = expiration)))
              authResult ← silhouette.env.authenticatorService.embed(value, response)
            } yield authResult

          case None ⇒ Future.failed(new Exception("todo: coundnt find user"))
        }
      }
    }.recoverTotal(badRequestWithMessage)
  }
}
