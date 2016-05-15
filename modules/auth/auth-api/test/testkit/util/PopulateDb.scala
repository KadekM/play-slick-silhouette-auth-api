package testkit.util

import auth.api.model.core.UserToken
import auth.api.service.Hasher
import auth.core.model.core._
import auth.direct.persistence.model.{LoginInfo, PasswordInfo}
import org.scalatestplus.play.OneAppPerSuite
import play.api.db.evolutions.Evolutions

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Populates database with data
  */
trait PopulateDb { this: OneAppPerSuite with TestAuthDatabaseAccess ⇒
  Evolutions.cleanupEvolutions(playAuthDb)
  Evolutions.applyEvolutions(playAuthDb)

  import driver.api._

  private val timeout = 30.seconds
  private val pwHasher = app.injector.instanceOf[Hasher]

  /* TODO
    // Users:
    val alice = User(UUID.randomUUID, "alice@wonderland.com", "Alice", "Wonder", User.State.Activated)
    val bob = User(UUID.randomUUID, "bob@wonderland.com", "Bob", "Wonder", User.State.Activated)

    db.run(tables.usersQuery ++= Seq(alice, bob))

    // LoginInfos and PasswordInfos (Silhouette specific):
    val act = for {
      _ ← tables.loginInfosQuery += LoginInfo(1, alice.uuid, CredentialsProvider.ID, alice.email)
      _ ← tables.passwordInfosQuery += PasswordInfo(1, "something",
      _ ← tables.loginInfosQuery += LoginInfo(2, bob.uuid, CredentialsProvider.ID, bob.email)
    } yield ()

    Await.ready(db.run(act), timeout)


  // Permissions to users:
  Await.ready(db.run(tables.permissionsToUsersQuery += PermissionToUser(AccessAdmin, alice.uuid)), timeout)
  */

  object Persistence {
    // We do not return the "inserted instance from db".
    // We could. We don't need it yet, so we don't.
    def insert[A](a: A)(implicit insA: Insertable[A]): A = {
      insA.insert(a)
      a
    }
  }

  // Some of these could be moved, along with insertable typeclass, to auth-core.
  implicit val userInsertable = new Insertable[User] {
    override def insert(a: User): Unit = Await.ready(db.run(tables.usersQuery += a), timeout)
  }

  implicit val userTokenInsertable = new Insertable[UserToken] {
    override def insert(a: UserToken): Unit = Await.ready(db.run(tables.userTokensQuery += a), timeout)
  }

  implicit val permissionsInsertable = new Insertable[Permission] {
    override def insert(a: Permission): Unit = Await.ready(db.run(tables.permissionsQuery += a), timeout)
  }

  implicit val permissionsToUsersInsertable = new Insertable[PermissionToUser] {
    override def insert(a: PermissionToUser): Unit = Await.ready(db.run(tables.permissionsToUsersQuery += a), timeout)
  }

  implicit val loginInfosInsertable = new Insertable[LoginInfo] {
    override def insert(a: LoginInfo): Unit = Await.ready(db.run(tables.loginInfosQuery += a), timeout)
  }

  implicit val passwordInfosInsertable = new Insertable[PasswordInfo] {
    override def insert(a: PasswordInfo): Unit = Await.ready(db.run(tables.passwordInfosQuery += a), timeout)
  }
}

trait Insertable[A] {
  def insert(a: A)
}
