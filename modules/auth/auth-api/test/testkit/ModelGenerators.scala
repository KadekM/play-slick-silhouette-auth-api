package testkit

import java.time.LocalDateTime

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.TokenAction
import auth.api.model.exchange.SignUp
import auth.core.model.core.{ Permission, PermissionToUser }
import org.scalacheck.Gen

trait ModelGenerators {
  private val strGen = (n: Int) ⇒ Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  implicit val signUpGen: Gen[SignUp] = for {
    firstName ← strGen(5)
    lastName ← strGen(6)
    email = firstName + "@" + lastName + ".com"
  } yield SignUp(email, firstName, lastName)

  implicit val userTokenActionGen: Gen[UserToken.UserTokenAction] =
    Gen.oneOf(TokenAction.ActivateAccount, TokenAction.ResetPassword)

  implicit val userTokenGen: Gen[UserToken] = for {
    token ← strGen(32)
    uuid ← Gen.uuid
    hoursToAdd ← Gen.chooseNum(-48, 48)
    date = LocalDateTime.now.plusHours(hoursToAdd)
    action ← userTokenActionGen
  } yield UserToken(token, uuid, date, action)
}
