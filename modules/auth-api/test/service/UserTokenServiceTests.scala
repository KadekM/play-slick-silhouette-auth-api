package service

import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.TokenAction.ActivateAccount
import auth.api.persistence.repo.UserTokenRepo
import auth.api.service.impl.UserTokenServiceImpl
import auth.core.persistence.model.AuthDatabaseConfigProvider
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import slick.dbio.SuccessAction
import testkit.util.UnitTest

class UserTokenServiceTests extends UnitTest with MockitoSugar {

  "UserTokenService" should {
    "return correct token when found" in new Fixture {
      val repo = mock[UserTokenRepo]
      val userToken = random[UserToken].copy(tokenAction = ActivateAccount)
      when(repo.find(userToken.token)).thenReturn(SuccessAction(Some(userToken)))
      when(repo.remove(userToken.token)).thenReturn(SuccessAction(true))

      val service = new UserTokenServiceImpl(dbConfigProvider, repo)

      service.claim(userToken.token).futureValue mustBe Some(userToken)
    }

    "call backing for action to store token" in new Fixture {
      val repo = mock[UserTokenRepo]
      val service = new UserTokenServiceImpl(dbConfigProvider, repo)

      val uuid = UUID.randomUUID
      service.issue(uuid, UserToken.TokenAction.ActivateAccount, forHours = 3)

      verify(repo, times(1)).issue(uuid, UserToken.TokenAction.ActivateAccount, 3)
    }
  }

  trait Fixture {
    val dbConfigProvider = app.injector.instanceOf[AuthDatabaseConfigProvider]
  }
}

