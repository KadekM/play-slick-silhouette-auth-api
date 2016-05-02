package util

import auth.api.persistence.repo.Hasher
import auth.core.model.core.{AccessAdmin, AccessBar}
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

  // Permissions:
  val permissions = Seq(AccessAdmin, AccessBar)
    .map(x ⇒ tables.permissionsQuery += x)
  permissions.map(db.run).foreach(Await.ready(_, timeout))

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

}
