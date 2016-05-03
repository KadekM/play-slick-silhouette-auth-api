package testkit.util

import auth.api.model.core.UserToken
import auth.core.testkit.{AuthCoreGenerators, Linker}
import org.scalacheck.Gen
import play.api.http.HttpVerbs
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.test.FakeRequest
import testkit.{ModelGenerators, ModelLinkers}

trait AuthSpecHelpers extends HttpVerbs with AuthCoreGenerators with ModelGenerators with ModelLinkers {
  implicit val ec = scala.concurrent.ExecutionContext.global

  implicit class FakeRequestPimper[A](r: FakeRequest[A]) {
    /**
      * Automatically sets headers and converts to Json
      * Use this to get around problem with deserializaiton when Action is of JsValue
      * TODO: resolve, why it doesnt properly deserialize (in regards to AnyContent/JsValue)? Is it bug?
      */
    def havingJsonBody[B](x: B)(implicit tjs: Writes[B]): FakeRequest[JsValue] =
      r.withBody(Json.toJson(x))
    //.withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
  }

  /**
    * Generates random instance
    */
  def random[T]()(implicit gen: Gen[T]): T =
    gen.sample.get

  /**
    * Generates random instances of type A and B, such that they are linked (i.e. one must supply the id
    * of other because of foreign key).
    */
  def randomLinked[R, A]()(implicit genR: Gen[R], genA: Gen[A], linkAtoR: Linker[R, A]): (R, A) = {
    val r = genR.sample.get
    val a = genA.sample.get
    (r, linkAtoR.link(r, a))
  }


  def FakePostRequest() = FakeRequest(POST, "")
  def FakeGetRequest() = FakeRequest(GET, "")
}

