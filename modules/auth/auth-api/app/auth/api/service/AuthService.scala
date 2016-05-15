package auth.api.service

import java.util.UUID

import auth.api.model.exchange.{SignUp, Token}
import auth.api.service.AuthService.{SignInError, SignedIn, UserCreated, UserCreationError}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import play.api.mvc.RequestHeader

import scala.concurrent.Future
import scalaz._

trait AuthService {

  /**
    * Creates user
    * todo: validForHours to duration instead of Long
    */
  def createUserByCredentials(
      signUp: SignUp, validForHours: Long): Future[UserCreationError \/ UserCreated]

  /**
    * Signs in user with identifier (such as credentials, and password)
    */
  def signIn(identifier: String, password: String)(
      implicit request: RequestHeader): Future[SignInError \/ SignedIn]
}

object AuthService {
  final case class UserCreated(uuid: UUID, token: String)
  sealed trait UserCreationError
  case object UserAlreadyExists extends UserCreationError

  final case class SignedIn(token: Token) extends AnyVal
  sealed trait SignInError
  case object UserNotActivated   extends SignInError
  case object LoginInfoMissing   extends SignInError
  case object InvalidCredentials extends SignInError
  final case class ProviderError(e: ProviderException) extends SignInError
}
