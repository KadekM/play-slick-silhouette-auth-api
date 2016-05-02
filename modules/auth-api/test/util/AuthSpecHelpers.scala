package util

import auth.api.model.exchange.SignUp
import play.api.http.HttpVerbs
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.test.FakeRequest

trait AuthSpecHelpers extends RandomGeneratorInstances with HttpVerbs {
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

  def random[T]()(implicit gen: RandomGenerator[T]): T = {
    gen.generate
  }

  def FakePostRequest() = FakeRequest(POST, "")
  def FakeGetRequest() = FakeRequest(GET, "")
}

trait RandomGeneratorInstances {
  private[this] val random = new scala.util.Random()

  implicit val signUpRandom = new RandomGenerator[SignUp] {
    override def generate: SignUp = {
      val firstName = random.nextString(5)
      val lastName = random.nextString(6)
      val email = firstName + "@"+lastName+".com"
      SignUp(email, firstName, lastName)
    }
  }
}

trait RandomGenerator[T] {
  def generate: T
}
