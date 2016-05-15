package auth.core.util

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Configuration
import play.api.http.Status
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Remark:
  * Only class why we need to depend to play's filters. But that's fine, although we could do it more lightweight
  * if we require.
  */
/**
  * Filter which enables functionality like REMEMBER ME during login, or
  * single login/logout across multiple frontend projects
  *
  * If authentication token is present with request, it sets the cookie to remember it.
  * If it's not present, it tries to supply it using that cookie
  */
class CookieAuthFilter @Inject()(
    config: Configuration,
    override implicit val mat: Materializer
)(implicit ec: ExecutionContext)
    extends Filter {

  private[this] val cookie = new CookieSettings(config)
  if (!cookie.secure || !cookie.httpOnly) {
    System.err.println(
        "Check your cookie settings (filters.cookieauth.cookie), as they are not secure!")
  }

  override def apply(f: (RequestHeader) ⇒ Future[Result])(rh: RequestHeader): Future[Result] = {
    rh.headers.get(cookie.tokenHeader) match {
      // Token was sent - discard current token cookie, and set it to new value
      case Some(token) ⇒
        f(rh).map { result ⇒
          val withoutCookie = result.discardingCookies(DiscardingCookie(cookie.name))

          // Have we been denied entry? If yes, discard our cookie
          if (isDeniedEntry(result)) withoutCookie
          else withoutCookie.withCookies(cookie.make(token))
        }

      // Token was not sent - look into cookies and set to header
      case None ⇒
        rh.cookies.get(cookie.name) match {
          // Cookie was found - transform it into X-Auth-Token
          case Some(found) ⇒
            val newRh  = rh.copy(headers = rh.headers.add(cookie.tokenHeader → found.value))
            val result = f(newRh)
            // Have we been denied entry in our result? If yes, discard cookie we sent (as it is invalid)
            result.map { withCookie ⇒
              if (isDeniedEntry(withCookie))
                withCookie.discardingCookies(DiscardingCookie(cookie.name))
              else withCookie
            }

          // Cookie was not found - process as you would 
          case None ⇒
            f(rh)
        }
    }
  }

  /**
    * Was request forbidden?
    */
  private def isDeniedEntry(result: Result): Boolean =
    result.header.status == Status.FORBIDDEN || result.header.status == Status.UNAUTHORIZED
}

class CookieSettings @Inject()(config: Configuration) {
  val name        = config.underlying.getString("filters.cookieauth.cookie.name")
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
  val secure   = config.underlying.getBoolean("filters.cookieauth.cookie.secure")
  val httpOnly = config.underlying.getBoolean("filters.cookieauth.cookie.httpOnly")

  def make(value: String): Cookie =
    Cookie(name, value, maxAge, path, domain, secure, httpOnly)
}
