package controllers

import java.time.LocalDateTime

import com.google.inject.Inject
import model.core.{User, UserToken}
import model.exchange.{Bad, Good}
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Result}
import service.{UserService, UserTokenService}

class TokenController @Inject() (userService: UserService,
                                 userTokenService: UserTokenService,
                                 translate: MessagesApi) extends Controller {

  import model.exchange.format.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  /**
    * Tries to validate token and execute token (which can be activation, or reseting password...)
    */
  def execute(token: String) = Action.async { implicit request ⇒
    userTokenService.claim(token).map {
      case Some(claimedToken) =>
        if (!isTokenExpired(claimedToken)) {
          claimedToken.tokenAction match {
            case UserToken.TokenAction.ActivateAccount =>
              activateAccount(claimedToken)
            case UserToken.TokenAction.ResetPassword =>
              resetPassword(claimedToken)
          }
        } else NotFound(Json.toJson(Bad(message = "token.invalid")))
      case None               ⇒ NotFound(Json.toJson(Bad(message = "token.invalid")))
    }
  }

  private def isTokenExpired(token: UserToken): Boolean = token.expiresOn.isBefore(LocalDateTime.now)

  private def activateAccount(token: UserToken): Result = {
    userService.setState(token.userUuid, User.State.Activated)
    Ok(Json.toJson(Good(translate("token.ok"))))
  }

  private def resetPassword(token: UserToken): Result = {
    //TODO implement
    Ok(Json.toJson(Good(translate("not.implemented"))))
  }
}

