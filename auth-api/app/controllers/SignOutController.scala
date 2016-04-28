package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, DiscardingCookie}

class SignOutController extends Controller {

  /**
    * Signs out the users
    */
  def signOut: Action[AnyContent] = Action {
    //TODO: set from common jwt stuff
    Ok(Json.toJson("todo")).discardingCookies(DiscardingCookie("jwt_token", domain = Some("fofobar.com")))
  }
}
