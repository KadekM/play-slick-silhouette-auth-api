package auth.api.controllers

import com.google.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

class SignInSocialController @Inject() extends Controller {

  /**
    * Signs in using providers such as Google or Facebook
    */
  def signIn(provider: String): Action[AnyContent] = ???
}
