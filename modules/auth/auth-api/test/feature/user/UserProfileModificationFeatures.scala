package feature.user

import java.util.UUID

import auth.api.controllers._
import auth.api.model.exchange._
import auth.core.model.core.Permission.AccessAdmin
import auth.core.model.core._
import play.api.libs.json.{JsSuccess, Json}
import play.api.test.Helpers._
import testkit.util.IntegrationTest

import scala.concurrent.Future

class UserProfileModificationFeatures extends IntegrationTest {
  import auth.api.formatting.exchange.Rest._
  import auth.core.formatting.core.Rest._

  "User" should {

    //TODO: more tests on modifying identifier
    "signup and modify profile self profile" in new Fixture {
      val userSignUp = random[SignUp]
      val signUpReq = FakePostRequest().havingJsonBody(userSignUp)
      val signUpF = usersController.signUpRequestRegistration()(signUpReq)
      val userUuid = lastResourceFromLocationHeader(signUpF).get

      val modifyReq = FakePutRequest()
        .havingJsonBody(UpdateUser(firstName = Some("Marko"), lastName = Some("MarkoL"), email = None))
      val modifyF = usersController.update(userUuid)(modifyReq)

      waitForDb()

      val getF = usersController.get(userUuid)(FakeGetRequest())
      val contentF = contentAsJson(getF).validate[User]

      contentF mustBe a[JsSuccess[_]]
      contentF.map { u ⇒
        u.firstName mustEqual "Marko"
        u.lastName mustEqual "MarkoL"
      }
    }

    "signup and modify email, deactivating account" ignore new Fixture {
    }
    //TODO: more tests on modifying identifier

    "can't modify someone else's profile" ignore new Fixture {}

  }

  trait Fixture {
    val usersController = app.injector.instanceOf[UsersController]
    val tokensController = app.injector.instanceOf[TokensController]
    val signInController = app.injector.instanceOf[SignInCredentialsController]
    val verifyController = app.injector.instanceOf[VerifyController]
    val permissionsController = app.injector.instanceOf[PermissionsController]
  }
}
