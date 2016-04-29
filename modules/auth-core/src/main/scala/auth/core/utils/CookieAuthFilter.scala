package auth.core.utils

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.Future

/**
  * Filter which enables functionality like REMEMBER ME during login, or
  * single login/logout across multiple frontend projects
  *
  * If authentication token is present with request, it sets the cookie to remember it.
  * If it's not present, it tries to supply it using that cookie
  */
class CookieAuthFilter @Inject() (config: Configuration,
    override implicit val mat: Materializer) extends Filter {

  private[this] val cookie = CookieSettings(config)
  if (!cookie.secure || !cookie.httpOnly)
    System.err.println("Check your cookie settings (filters.cookieauth.cookie), as they are not secure!")

  override def apply(f: (RequestHeader) ⇒ Future[Result])(rh: RequestHeader): Future[Result] = {
    // TODO: ec
    import scala.concurrent.ExecutionContext.Implicits.global

    println("cookies - " + rh.cookies.mkString(","))

    rh.headers.get(cookie.tokenHeader) match {
      // Token was sent - discard current token cookie, and set it to new value
      case Some(token) ⇒
        println("got token, discard cookies and make new ones")
        f(rh).map { result ⇒
          result.discardingCookies(DiscardingCookie(cookie.name))
            .withCookies(cookie.make(token))
        }

      // Token was not sent - look into cookies and set to header
      case None ⇒
        println("no token, get cookies")
        rh.cookies.get(cookie.name) match {
          // Cookie was found - transform it into X-Auth-Token
          case Some(found) ⇒
            println("cookies found, copying to header")
            val newRh = rh.copy(headers = rh.headers.add(cookie.tokenHeader -> found.value))
            f(newRh)

          // Cookie was not found - process as you would 
          case None ⇒
            println("no cookies found")
            f(rh)
        }
    }
  }
}

case class CookieSettings @Inject() (config: Configuration) {
  val name = config.underlying.getString("filters.cookieauth.cookie.name")
  val tokenHeader = config.underlying.getString("filters.cookieauth.cookie.token.header")
  val maxAge = {
    val age = config.underlying.getInt("filters.cookieauth.cookie.maxage")
    if (age >= 0) Some(age) else None
  }
  val path = config.underlying.getString("filters.cookieauth.cookie.path")
  val domain = {
    val domain = config.underlying.getString("filters.cookieauth.cookie.domain")
    if (domain.length >= 0) Some(domain) else None
  }
  val secure = config.underlying.getBoolean("filters.cookieauth.cookie.secure")
  val httpOnly = config.underlying.getBoolean("filters.cookieauth.cookie.httpOnly")

  def make(value: String): Cookie =
    Cookie(name, value, maxAge, path, domain, secure, httpOnly)
}
