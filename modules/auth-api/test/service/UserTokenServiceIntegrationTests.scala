package service

import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.api.service.UserTokenService
import auth.core.model.core.User
import testkit.util.IntegrationTest

import scala.concurrent.Await
import scala.concurrent.duration._

class UserTokenServiceIntegrationTests extends IntegrationTest {

  import driver.api._

  "UserToken" should {
    "add token to database when issued" in new Fixture {
      val user = random[User]
      persistence.insert(user)

      val issued = Await.result(tokenService.issue(user.uuid, random[UserTokenAction], forHours = 3), 10.seconds)

      val lookForTokenInDb = tables.userTokensQuery.filter(_.token === issued.token).result.headOption

      val fromDb = Await.result(db.run(lookForTokenInDb), 10.seconds)

      fromDb must not be empty
      fromDb.get mustBe issued
    }

    "return and claim corrent token from database" in new Fixture {
      val (user, userToken) = randomLinked[User, UserToken]
      persistence.insert(user)
      persistence.insert(userToken)

      val token = tokenService.claim(userToken.token)
      token.futureValue.get mustBe userToken
    }
  }

  trait Fixture {
    val tokenService = app.injector.instanceOf[UserTokenService]
  }
}
