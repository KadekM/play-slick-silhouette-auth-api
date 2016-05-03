package testkit

import java.time.LocalDateTime

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.TokenAction
import auth.api.model.exchange.SignUp
import org.scalacheck.{ Arbitrary, Gen }

trait ModelGenerators {
  private val strGen = (n: Int) ⇒ Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  implicit val signUpGen: Gen[SignUp] = for {
    firstName ← strGen(5)
    lastName ← strGen(6)
    email = firstName + "@" + lastName + ".com"
  } yield SignUp(firstName, lastName, email)

  implicit val userTokenAction: Gen[UserToken.UserTokenAction] =
    Gen.oneOf(TokenAction.ActivateAccount, TokenAction.ResetPassword)

  implicit val userTokenGen: Gen[UserToken] = for {
    token ← strGen(32)
    uuid ← Gen.uuid
    hoursToAdd ← Gen.chooseNum(-48, 48)
    date = LocalDateTime.now.plusHours(hoursToAdd)
    action ← userTokenAction
  } yield UserToken(token, uuid, date, action)
}
