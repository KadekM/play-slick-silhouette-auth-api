package auth.api.controllers

import auth.core.util.CookieSettings
import com.google.inject.Inject
import play.api.mvc.Results.EmptyContent
import play.api.mvc.{Action, AnyContent, Controller, DiscardingCookie}

class SignOutController @Inject()(authCookieSettings: CookieSettings) extends Controller {

  import auth.api.formatting.exchange.Rest._

  /**
    * Signs out the users
    */
  def signOut: Action[AnyContent] = Action {
    Ok(EmptyContent()).discardingCookies(DiscardingCookie(authCookieSettings.name))
  }
}
