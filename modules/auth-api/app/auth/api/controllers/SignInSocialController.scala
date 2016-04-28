package auth.api.controllers

import play.api.mvc.{Action, AnyContent, Controller}

class SignInSocialController extends Controller {

  /**
    * Signs in using providers such as Google or Facebook
    */
  def signIn(provider: String): Action[AnyContent] = ???
}
