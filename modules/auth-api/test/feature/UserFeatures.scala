package feature

import java.util.UUID

import auth.api.controllers._
import auth.api.model.exchange._
import auth.core.model.core.AccessAdmin
import play.api.libs.json.Json
import play.api.test.Helpers._
import testkit.util.IntegrationTest

class UserFeatures extends IntegrationTest {
  import auth.api.formatting.exchange.rest._

  "User" should {
    "be able to sign up" in new Fixture {
      val signUpReq1 = FakePostRequest()
        .havingJsonBody(random[SignUp])
      val signUpF1 = usersController.signUpRequestRegistration()(signUpReq1)
      status(signUpF1) mustBe CREATED
    }

    "not be able to sign up with empty identifier" ignore new Fixture {
      val signUpReq1 = FakePostRequest()
        .havingJsonBody(SignUp("", "foo", "bar"))
      val signUpF1 = usersController.signUpRequestRegistration()(signUpReq1)
      status(signUpF1) must not be CREATED
    }

    "not be able to sign up with invalid identifier" ignore new Fixture {
    }

    "not be able to sign up twice with same identifier" in new Fixture {
      val sharedSignUp = random[SignUp]
      val signUpReq1 = FakePostRequest()
        .havingJsonBody(sharedSignUp)
      val signUpF1 = usersController.signUpRequestRegistration()(signUpReq1)
      status(signUpF1) mustBe CREATED

      waitForDb()

      val signUpReq2 = FakePostRequest()
        .havingJsonBody(sharedSignUp)
      val signUpF2 = usersController.signUpRequestRegistration()(signUpReq2)
      status(signUpF2) mustBe CONFLICT
    }

    "not be able to log in without creating password before" in new Fixture {
      val signUp = random[SignUp]
      val signUpReq1 = FakePostRequest()
        .havingJsonBody(signUp)
      val signUpF1 = usersController.signUpRequestRegistration()(signUpReq1)
      status(signUpF1) mustBe CREATED

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(signUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      status(signInF) mustBe UNAUTHORIZED
    }

    "signup and create password successfully" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      status(tokenExecuteF) mustBe OK
    }

    "signup and fail to create empty password" ignore new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      status(tokenExecuteF) must not be OK
    }

    "signup and fail to create weak passowrd" ignore new Fixture {
    }

    "signup and fail to create password for non-existing token" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("")))
      val tokenExecuteF = tokensController.execute("this-token-does-not-exist")(tokenExecuteReq)

      status(tokenExecuteF) must not be OK
    }

    "signup, create password and login successfully (getting back auth token)" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      status(signInF) mustBe OK
      header(jwtHeaderFieldName, signInF) must not be empty
      header(jwtHeaderFieldName, signInF).get must not be empty
    }

    "signup, create password and fail to login because of invalid identifier" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn("i-have-never-registered", "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      status(signInF) mustBe UNAUTHORIZED
      header(jwtHeaderFieldName, signInF) mustBe empty
    }

    "signup, create password and fail to login because of invalid password" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "123WrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      status(signInF) mustBe UNAUTHORIZED
      header(jwtHeaderFieldName, signInF) mustBe empty
    }

    "signup, create password, login and verify successfully" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
            .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
          .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      val tokenValue = header(jwtHeaderFieldName, signInF).get
      val verifyReq = FakeGetRequest()
          .withHeaders(jwtHeaderFieldName -> tokenValue)
      val verifyF = verifyController.verify(verifyReq)

      status(verifyF) mustEqual OK
    }

    "signup, create password, login and fail to verify without of token on user being unauthenticated" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      val verifyReq = FakeGetRequest()
      val verifyF = verifyController.verify(verifyReq)

      status(verifyF) mustEqual UNAUTHORIZED
    }

    "signup, create password, login and fail to admin-verify without of token on user being unauthenticated" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      val verifyReq = FakeGetRequest()
      val verifyF = verifyController.verifyAdmin(verifyReq)

      status(verifyF) mustEqual UNAUTHORIZED
    }

    "signup, create password, login and fail to admin-verify on user being authenticated, but unauthorized" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      val tokenValue = header(jwtHeaderFieldName, signInF).get
      val verifyReq = FakeGetRequest()
        .withHeaders(jwtHeaderFieldName -> tokenValue)
      val verifyF = verifyController.verifyAdmin(verifyReq)

      status(verifyF) mustEqual FORBIDDEN
    }

    "signup, create password, login, is granted admin permission (by admin)" +
      " and is successfully admin authorized" ignore new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val signUpResp = contentAsJson(signUpF).as[Good]
      val token = (signUpResp.details.get \ "token").get.as[String]

      waitForDb()

      val tokenExecuteReq = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("MyStrongPassword")))
      val tokenExecuteF = tokensController.execute(token)(tokenExecuteReq)

      waitForDb()

      val signInReq = FakePostRequest()
        .havingJsonBody(SignIn(userSignUp.identifier, "MyStrongPassword", rememberMe = false))
      val signInF = signInController.signIn(signInReq)

      val extractedUserFromLocation = header("Location", signUpF)
        .map(_.split("/").last).get
      val userUuid = UUID.fromString(extractedUserFromLocation)

      val grantReq = FakePostRequest()
        .withHeaders(jwtHeaderFieldName -> "TODO: ADMIN_TOKEN")
      val grantAdmin = PermissionUserPair(AccessAdmin, userUuid)
      val grantAdminReq = FakePostRequest().havingJsonBody(grantAdmin)
      permissionsController.grant(grantAdminReq)

      waitForDb()

      val tokenValue = header(jwtHeaderFieldName, signInF).get
      val verifyReq = FakeGetRequest()
        .withHeaders(jwtHeaderFieldName -> tokenValue)
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
