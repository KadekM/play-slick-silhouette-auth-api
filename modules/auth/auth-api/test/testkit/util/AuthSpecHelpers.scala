package testkit.util

import akka.util.Timeout
import auth.api.model.core.UserToken
import auth.core.testkit.AuthCoreGenerators
import auth.direct.testkit.Linker
import org.scalacheck.Gen
import play.api.http.{HeaderNames, HttpVerbs}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.{FakeRequest, Helpers, ResultExtractors}
import testkit.{ModelGenerators, ModelLinkers}

import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.test.Helpers._

trait AuthSpecHelpers extends AuthCoreGenerators with ModelGenerators with ModelLinkers {
  implicit val ec = scala.concurrent.ExecutionContext.global

  /**
    * Extracts last resource from headers location.
    * I.e., if location of new user is /users/some-uuid,
    * it will return Some(some-uuid)
    */
  def lastResourceFromLocationHeader(f: Future[Result])(implicit timeout: Timeout): Option[String] =
    Helpers.header(LOCATION, f)(timeout).map(_.split("/").last)


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

  def FakePostRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, "")
  def FakeGetRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, "")
  def FakePutRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(PUT, "")
  def FakeDeleteRequest(): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(DELETE, "")
}

