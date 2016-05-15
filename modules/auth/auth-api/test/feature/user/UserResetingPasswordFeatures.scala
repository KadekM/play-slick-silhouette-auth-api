package feature.user

import auth.api.controllers._
import testkit.util.IntegrationTest
import play.api.test._
import play.api.test.Helpers._

class UserResetingPasswordFeatures extends IntegrationTest {

  "User" should {
    "signup, create password, request reseting password, and still sign in with old password" ignore new Fixture {}

    "signup, create password, reset password, and fail to log in with old password" ignore new Fixture {}

    "signup, create password, reset password, and log in with new password" ignore new Fixture {}
  }

  trait Fixture {
    val usersController = app.injector.instanceOf[UsersController]
    val tokensController = app.injector.instanceOf[TokensController]
    val signInController = app.injector.instanceOf[SignInCredentialsController]
    val verifyController = app.injector.instanceOf[VerifyController]
    val permissionsController = app.injector.instanceOf[PermissionsController]
  }
}
