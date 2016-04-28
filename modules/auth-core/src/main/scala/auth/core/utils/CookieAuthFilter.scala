package auth.core.utils

import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.Future

/**
  * Filter which enables functionality like REMEMBER ME during login, or
  * single login/logout across multiple frontend projects
  *
  * If authentication token is present with request, it sets the cookie to remember it.
  * If it's not present, it tries to supply it using that cookie
  */
class CookieAuthFilter @Inject()(override implicit val mat: Materializer) extends Filter {
  override def apply(f: (RequestHeader) ⇒ Future[Result])(rh: RequestHeader): Future[Result] = {
    // TODO: header name from silhouette
    // TODO: cookie name and other stuff to config
    // TODO: secure the cookies, https, etc
    // TODO: document better
    // TODO: ec
    import scala.concurrent.ExecutionContext.Implicits.global

    val cookieName = "jwt_token"
    val xAuthToken = "X-Auth-Token"
    println("cookies - "+rh.cookies.mkString(","))

    rh.headers.get(xAuthToken) match {
      // Token was sent - discard current token cookie, and set it to new value
      case Some(token) ⇒
        println("got token, discard cookies and make new ones")
        f(rh).map { result ⇒
          result.discardingCookies(DiscardingCookie(cookieName))
            .withCookies(Cookie(cookieName, token, domain = Some("fofobar.com")))
        }

      // Token was not sent - look into cookies and set to header
      case None ⇒
        println("no token, get cookies")
        rh.cookies.get(cookieName) match {
          // Cookie was found - transform it into X-Auth-Token
          case Some(found) ⇒
            println("cookies found, copying to header")
            val newRh = rh.copy(headers = rh.headers.add(xAuthToken -> found.value))
            f(newRh)

          // Cookie was not found - process as you would 
          case None ⇒
            println("no cookies found")
            f(rh)
        }
    }
  }
}

