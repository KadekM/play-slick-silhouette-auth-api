package controllers

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.{ LoginInfo, Silhouette }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.core.User
import model.exchange.{ Bad, Good, SignUp }
import play.api.i18n.MessagesApi
import play.api.libs.json.{ JsError, JsString, JsValue, Json }
import play.api.mvc.{ Action, Controller }
import service.UserService
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * Sign up user to the system
  */
class SignUpController @Inject() (silhouette: Silhouette[DefaultEnv],
    passwordHasher: PasswordHasher,
    messagesApi: MessagesApi,
    userService: UserService,
    authInfoRepository: AuthInfoRepository) extends Controller with ResponseHelpers {

  import model.exchange.format.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[SignUp].map { signUp ⇒
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)

      userService.retrieve(loginInfo).flatMap {
        case Some(user) ⇒
          Future.successful(BadRequest(Json.toJson(Bad(message = messagesApi("user.exists")))))

        case None ⇒
          val authInfo = passwordHasher.hash(signUp.password)
          val user = User(UUID.randomUUID.toString, loginInfo, signUp.identifier, signUp.firstName, signUp.lastName,
            User.State.Created)

          for {
            user ← userService.save(user)
            authInfo ← authInfoRepository.add(loginInfo, authInfo)
          } yield {
            Ok(Json.toJson(Good(user.uuid.toString))) // todo: token
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }
}
