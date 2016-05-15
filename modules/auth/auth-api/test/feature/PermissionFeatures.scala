package feature

import java.util.UUID
import auth.api.controllers._
import auth.api.model.exchange._
import auth.core.model.core._
import play.api.libs.json.Json
import play.api.test.Helpers._
import testkit.util.IntegrationTest

class PermissionFeatures extends IntegrationTest {
  import auth.api.formatting.exchange.Rest._
  import auth.core.formatting.core.Rest._

  "Permission" should {
    "be granted" in new Fixture {
      val user = Persistence.insert(random[User])
      val grantPermissionReq = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(Permission.AccessAdmin)))

      val grantPermissionF = permissionsController.grant(user.uuid.toString)(grantPermissionReq)

      status(grantPermissionF) mustEqual CREATED
    }

    "granting permission yield location header" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission = Permission.AccessAdmin
      val grantPermissionReq = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission)))

      val grantPermissionF = permissionsController.grant(user.uuid.toString)(grantPermissionReq)

      status(grantPermissionF) mustEqual CREATED
      header(LOCATION, grantPermissionF) must not be empty
      header(LOCATION, grantPermissionF).get must include (user.uuid.toString)
      header(LOCATION, grantPermissionF).get must include (permission.toString)
    }

    "not be granted twice the same" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission = Permission.AccessAdmin
      val grantPermissionReq1 = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission)))
      val grantPermissionF1 = permissionsController.grant(user.uuid.toString)(grantPermissionReq1)

      grantPermissionF1.futureValue

      val grantPermissionReq2 = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission)))
      val grantPermissionF2 = permissionsController.grant(user.uuid.toString)(grantPermissionReq2)

      status(grantPermissionF2) mustEqual CONFLICT
    }

    "be granted two different" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission1 = Permission.AccessAdmin
      val grantPermissionReq1 = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission1)))
      val grantPermissionF1 = permissionsController.grant(user.uuid.toString)(grantPermissionReq1)

      val permission2 = Permission.AccessBar
      val grantPermissionReq2 = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission2)))
      val grantPermissionF2 = permissionsController.grant(user.uuid.toString)(grantPermissionReq2)

      status(grantPermissionF1) mustEqual CREATED
      status(grantPermissionF2) mustEqual CREATED
    }

    "be granted only by Admin role" ignore new Fixture {}

    "be revoked" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission = Permission.AccessAdmin
      val grantPermissionReq = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission)))
      val grantPermissionF = permissionsController.grant(user.uuid.toString)(grantPermissionReq)

      grantPermissionF.futureValue
      waitForDb()

      val revokePermissionF = permissionsController.revoke(permission.toString, user.uuid.toString)(FakePostRequest())

      status(revokePermissionF) mustEqual OK
    }

    "not be revoked if was never granted" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission = Permission.AccessAdmin
      val revokePermissionF = permissionsController.revoke(permission.toString, user.uuid.toString)(FakePostRequest())

      status(revokePermissionF) mustEqual NOT_FOUND
    }

    "not be revoked twice the same" in new Fixture {
      val user = Persistence.insert(random[User])
      val permission = Permission.AccessAdmin
      val grantPermissionReq = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(permission)))
      val grantPermissionF = permissionsController.grant(user.uuid.toString)(grantPermissionReq)

      grantPermissionF.futureValue

      val revokePermissionF1 = permissionsController.revoke(permission.toString, user.uuid.toString)(FakePostRequest())
      status(revokePermissionF1) mustEqual OK

      waitForDb()

      val revokePermissionF2 = permissionsController.revoke(permission.toString, user.uuid.toString)(FakePostRequest())
      status(revokePermissionF2) mustEqual NOT_FOUND
    }

    "be revoked only by Admin role" ignore new Fixture {}

    "have more than zero possibles" in new Fixture {
      val listF = permissionsController.listPossible()(FakeGetRequest())

      contentAsJson(listF).as[Seq[Permission]].size must be > 0
    }

    "be listed ok" in new Fixture {
      val listF = permissionsController.listPossible()(FakeGetRequest())

      status(listF) mustEqual OK
      contentAsJson(listF).as[Seq[Permission]].size mustEqual Permission.allPermissions.size
    }

    "assigning new permission correctly adds it" in new Fixture {
      val (user, permissionToUser) = randomLinked[User, PermissionToUser]

      val listF1 = permissionsController.listAssigned()(FakeGetRequest())
      val beforeAmount = contentAsJson(listF1).as[Seq[PermissionToUser]].size

      Persistence.insert(user)
      Persistence.insert(permissionToUser)

      val listF2 = permissionsController.listAssigned()(FakeGetRequest())
      val after = contentAsJson(listF2).as[Seq[PermissionToUser]]
      val afterAmount = after.size

      afterAmount must equal (beforeAmount + 1)
      after must contain(permissionToUser)
    }
  }

  trait Fixture {
    val permissionsController = app.injector.instanceOf[PermissionsController]
  }
}
