package auth.api.controllers

import auth.core.utils.CookieSettings
import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, DiscardingCookie}

class SignOutController @Inject() (authCookieSettings: CookieSettings) extends Controller {

  /**
    * Signs out the users
    */
  def signOut: Action[AnyContent] = Action {
    Ok(Json.toJson("todo")).discardingCookies(DiscardingCookie(authCookieSettings.name))
  }
}