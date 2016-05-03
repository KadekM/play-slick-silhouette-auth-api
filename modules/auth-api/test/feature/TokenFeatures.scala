package feature

import java.time.LocalDateTime
import java.util.UUID

import auth.api.controllers.TokensController
import auth.api.model.core.UserToken
import auth.api.model.exchange.CreatePassword
import auth.core.model.core.User
import auth.core.model.core.User.UserState
import auth.core.persistence.model.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json.Json
import testkit.util.IntegrationTest
import play.api.test.Helpers._
import slick.dbio.Effect.Read
import slick.profile.{FixedSqlStreamingAction, SqlAction}

class TokenFeatures extends IntegrationTest {
  import driver.api._
  import auth.api.formatting.exchange.rest._

  "Token" should {
    "be consumed if it exists and activate user if it's of proper action" in new Fixture {
      val (user, token) = prepareActivateAccountTokenForUser("token1234")

      val req = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("SomeStrongPassword123")))
      val resp = tokensController.execute("token1234")(req)

      status(resp) mustBe OK
    }

    "not be able to be consumed twice" in new Fixture {
      val (user, token) = prepareActivateAccountTokenForUser("token5")

      val req1 = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("SomeStrongPassword123")))
      val resp1 = tokensController.execute("token5")(req1)
      status(resp1) mustBe OK

      val resp2 = tokensController.execute("token5")(FakePostRequest())
      status(resp2) mustBe NOT_FOUND
    }

    "return not found if it doesn't exist" in new Fixture {
      val (user, token) = prepareActivateAccountTokenForUser("token5555")

      val resp = tokensController.execute("notexisting")(FakePostRequest())

      status(resp) mustBe NOT_FOUND
    }

    "not found expired token" in new Fixture {
      val (user, token) = prepareActivateAccountTokenForUser("token_expired",
        expiration = LocalDateTime.now.minusDays(1))

      val resp = tokensController.execute("token_expired")(FakePostRequest())

      status(resp) mustBe NOT_FOUND
    }
  }

  "TokenAction ActivateAccount" should {
    "activate users's account" in new Fixture {
      val (user, token) = prepareActivateAccountTokenForUser("activate_token")

      val req = FakePostRequest()
        .withJsonBody(Json.toJson(CreatePassword("SomeStrongPassword123")))
      val resp = tokensController.execute("activate_token")(req)

      waitForDb()

      import tables._
      val userState = tables.usersQuery
        .filter(_.uuid === user.uuid)
        .map(_.state).result.headOption

      db.run(userState).futureValue.get mustBe User.State.Activated
    }
  }

  trait Fixture {
    val tokensController = app.injector.instanceOf[TokensController]

    def prepareActivateAccountTokenForUser(tokenValue: String, expiration: LocalDateTime = LocalDateTime.now.plusDays(1)) = {
      val user = User(UUID.randomUUID, s"foo@$tokenValue.com", "foo", "bar", User.State.Created)
      val token = UserToken(tokenValue, user.uuid, expiration, UserToken.TokenAction.ActivateAccount)
      val prepare = for {
        _ ← tables.usersQuery += user
        _ ← tables.loginInfosQuery += LoginInfo(-1, user.uuid, CredentialsProvider.ID, user.email)
        _ ← tables.userTokensQuery += token
      } yield ()

      db.run(prepare).futureValue

      waitForDb()

      (user, token)
    }
  }
}
