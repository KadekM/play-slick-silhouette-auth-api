package auth.api.service.impl

import java.util.UUID

import auth.api.model.core.UserToken.TokenAction
import auth.api.model.exchange.{SignUp, Token}
import auth.api.service.AuthService._
import auth.api.service.{AuthService, UserTokenService}
import auth.api.utils.date.Conversions
import auth.core.DefaultEnv
import auth.core.model.core.User
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import auth.direct.persistence.model.dao.LoginInfoDao
import auth.direct.service.UserService
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json._
import play.api.mvc.RequestHeader

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

class AuthServiceImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider,
                      silhouette: Silhouette[DefaultEnv],
                      credentialsProvider: CredentialsProvider,
                      userService: UserService,
                      tokenService: UserTokenService,
                      loginInfoDao: LoginInfoDao)(implicit ec: ExecutionContext)
    extends AuthService with AuthDbAccess with CoreAuthTablesDefinitions {

  override def createUserByCredentials(
      signUp: SignUp, validForHours: Long): Future[UserCreationError \/ UserCreated] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)

    userService.retrieve(loginInfo).flatMap {
      case Some(user) ⇒
        Future.successful(-\/(UserAlreadyExists))
      case None ⇒
        val user = User(UUID.randomUUID,
                        signUp.identifier,
                        signUp.firstName,
                        signUp.lastName,
                        User.State.Created)

        for {
          // TODO: move to single transaction
          user ← userService.save(user) // and get rid of save on user service if possible, and users deps
          _    ← loginInfoDao.save(loginInfo, user.uuid)
          registrationToken ← tokenService.issue(
              user.uuid, TokenAction.ActivateAccount, validForHours)
        } yield {
          // TODO: remove token from here, do not return it, so users have to visit email - in email activate link is not link to api
          \/-(UserCreated(user.uuid, registrationToken.token))
        }
    }
  }

  override def signIn(identifier: String, password: String)
                     (implicit request: RequestHeader): Future[SignInError \/ SignedIn] = {
    val f: Future[(Option[User], LoginInfo)] = for {
      loginInfo <- credentialsProvider.authenticate(Credentials(identifier, password))
      maybeUser <- userService.retrieve(loginInfo)
    } yield (maybeUser, loginInfo)

    f.flatMap {
      case (Some(user), loginInfo) if user.state == User.State.Activated =>
        for {
          authenticator ← silhouette.env.authenticatorService
            .create(loginInfo)
            .map(_.copy(customClaims = Some(createCustomClaims(user))))

          tokenValue ← silhouette.env.authenticatorService.init(authenticator)
          expiration = Conversions.jodaToJava(authenticator.expirationDateTime)
        } yield {
          \/-(SignedIn(Token(token = tokenValue, expiresOn = expiration)))
        }

      case (Some(_), _) =>
        Future.successful(-\/(UserNotActivated))

      case (None, _) =>
        Future.successful(-\/(InvalidCredentials))
    }.recover {
      case e: ProviderException => -\/(ProviderError(e))
    }
  }

  /**
    * Creates custom claims for particular user to be embedded in jwt token
    */
  private def createCustomClaims(user: User): JsObject =
    Json.obj("user_uuid" -> user.uuid)
}
