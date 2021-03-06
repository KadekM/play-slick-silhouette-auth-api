package auth.api.controllers

import java.time.LocalDateTime

import auth.api.model.core.UserToken
import auth.api.model.exchange.{Bad, CreatePassword}
import auth.api.service.UserTokenService
import auth.core.model.core.User
import auth.direct.persistence.model.dao.LoginInfoDao
import auth.direct.service.UserService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json.Json
import play.api.mvc.Results.EmptyContent
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class TokensController @Inject()(
    passwordHasher: PasswordHasher,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    authInfoRepository: AuthInfoRepository)(implicit ec: ExecutionContext)
    extends Controller with ResponseHelpers {

  import auth.api.formatting.exchange.Rest._

  /**
    * Fetches the token and executes it (consumes it). It is run only when it's valid (not expired.
    * @note Does not consume tokens by default, only on successful request!
    */
  def execute(token: String): Action[AnyContent] = Action.async { implicit request ⇒
    userTokenService.find(token).flatMap {
      case Some(t) if !isTokenExpired(t.expiresOn) ⇒
        executeImpl(t)
      case Some(t) ⇒
        //todo: log that someone tried to access expired token
        Future.successful(NotFound(Json.toJson(Bad.empty)))
      case _ ⇒
        Future.successful(NotFound(Json.toJson(Bad.empty)))
    }
  }

  // TODO: extract to service
  /**
    * Do not forget to consume tokens upon successful request
    */
  private def executeImpl(userToken: UserToken)(
      implicit request: Request[AnyContent]): Future[Result] = userToken match {
    case UserToken(_, userUuid, expiresOn, UserToken.TokenAction.ActivateAccount) ⇒
      request.body.asJson match {
        case Some(json) ⇒
          json
            .validate[CreatePassword]
            .map { requestPw ⇒
              for {
                _          ← userTokenService.consume(userToken.token)
                _          ← userService.setState(userUuid, User.State.Activated)
                Some(user) ← userService.retrieve(userUuid)
                loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
                authInfo  = passwordHasher.hash(requestPw.password)
                _ ← authInfoRepository.add(loginInfo, authInfo)
              } yield {
                Created(EmptyContent())
              }
            }
            .recoverTotal(badRequestWithMessage)

        case None ⇒ Future.successful(BadRequest(Json.toJson(Bad.invalidJson)))
      }

    case UserToken(_, _, _, UserToken.TokenAction.ResetPassword) ⇒
      Future.successful(NotImplemented)
  }

  private def isTokenExpired(expiresOn: LocalDateTime): Boolean =
    expiresOn.isBefore(LocalDateTime.now)
}
