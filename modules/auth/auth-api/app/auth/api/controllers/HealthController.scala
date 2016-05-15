package auth.api.controllers

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}
import auth.api._

class HealthController extends Controller {
  def fetch: Action[AnyContent] = Action {
    val data: Map[String, String] = BuildInfo.toMap.mapValues {
      case v => v.toString
    }
    Ok(Json.toJson(data))
  }
}
