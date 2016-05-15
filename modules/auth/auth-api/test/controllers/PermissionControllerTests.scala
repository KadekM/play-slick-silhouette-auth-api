package controllers

import auth.api.controllers._
import auth.api.model.exchange.AssignPermission
import auth.core.model.core._
import play.api.libs.json.Json
import play.api.test.Helpers._
import testkit.util.UnitTest

class PermissionControllerTests extends UnitTest {
  import auth.api.formatting.exchange.Rest._

  "PermissionController" should {
    "on grant not crash with bad uuid" ignore new Fixture {
      val grantPermissionReq = FakePostRequest()
        .withJsonBody(Json.toJson(AssignPermission(Permission.AccessAdmin)))

      val grantPermissionF = permissionsController.grant("not-uuid")(grantPermissionReq)

      status(grantPermissionF) mustEqual BAD_REQUEST
    }

    "on grant not crash with non existing permission" ignore new Fixture {}
  }

  trait Fixture {
    val permissionsController = app.injector.instanceOf[PermissionsController]
  }
}
