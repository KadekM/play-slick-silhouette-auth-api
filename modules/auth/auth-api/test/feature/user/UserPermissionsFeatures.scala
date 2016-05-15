package feature.user

import java.util.UUID

import auth.api.controllers._
import auth.api.model.exchange.{ AssignPermission, CreatePassword, SignIn, SignUp }
import auth.core.model.core.Permission.AccessAdmin
import play.api.libs.json.Json
import testkit.util.IntegrationTest
import play.api.test._
import play.api.test.Helpers._

class UserPermissionsFeatures extends IntegrationTest {

  import auth.api.formatting.exchange.Rest._
  import auth.core.formatting.core.Rest._

  "User" should {

    "contain permissions" ignore new Fixture {}

    "signup, create password, login, is granted admin permission (by admin)" +
      " and is successfully admin authorized" ignore new Fixture {
        val userSignUp = random[SignUp]
        val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
        val signUpF = usersController.signUpRequestRegistration()(signUpReq)
        val signUpResp = contentAsJson(signUpF)
        val token = (signUpResp \ "token").get.as[String]

        waitForDb()

        val tokenExecuteReq = FakePostRequest()
          .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
        val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

        waitForDb()

        val signInReq = FakePostRequest()
          .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
        val signInF = signInController.signIn(signInReq)

        val extractedUserFromLocation = lastResourceFromLocationHeader(signInF).get
        val userUuid = UUID.fromString(extractedUserFromLocation)

        val grantReq = FakePostRequest()
          .withHeaders(jwtHeaderFieldName → "TODO: ADMIN_TOKEN")
        val grantAdmin = AssignPermission(AccessAdmin)
        val grantAdminReq = FakePostRequest().havingJsonBody(grantAdmin)
        permissionsController.grant(userUuid.toString)(grantAdminReq)

        waitForDb()

        val tokenValue = header(jwtHeaderFieldName, signInF).get
        val verifyReq = FakeGetRequest()
          .withHeaders(jwtHeaderFieldName → tokenValue)
        val verifyF = verifyController.verifyAdmin(verifyReq)

        status(verifyF) mustEqual OK
      }
  }

  trait Fixture {
    val usersController = app.injector.instanceOf[UsersController]
    val tokensController = app.injector.instanceOf[TokensController]
    val signInController = app.injector.instanceOf[SignInCredentialsController]
    val verifyController = app.injector.instanceOf[VerifyController]
    val permissionsController = app.injector.instanceOf[PermissionsController]
  }
}
