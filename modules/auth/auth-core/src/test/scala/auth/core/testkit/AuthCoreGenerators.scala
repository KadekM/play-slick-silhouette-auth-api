package auth.core.testkit

import auth.core.model.core._
import auth.core.model.core.User.UserState
import org.scalacheck.Gen

trait AuthCoreGenerators {
  private val strGen = (n: Int) ⇒ Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  implicit val userStateGen: Gen[UserState] =
    Gen.oneOf(User.State.Created, User.State.Activated, User.State.Deactivated)

  implicit val userGen: Gen[User] = for {
    uuid ← Gen.uuid
    firstName ← strGen(5)
    lastName ← strGen(6)
    email = firstName + "@" + lastName + ".com"
    state ← userStateGen
  } yield User(uuid, email, firstName, lastName, state)

  implicit val permissionGeneratorGen: Gen[Permission] = for {
    perm ← Gen.oneOf(Permission.allPermissions)
  } yield perm

  implicit val permissionToUserGen: Gen[PermissionToUser] = for {
    perm ← permissionGeneratorGen
    uuid ← Gen.uuid
  } yield PermissionToUser(perm, uuid)
}