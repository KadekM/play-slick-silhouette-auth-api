package utils

import javax.inject.Inject

import akka.stream.Materializer
import play.api.http.HttpFilters
import play.api.mvc._
import play.filters.cors.CORSFilter
import play.filters.headers.SecurityHeadersFilter

import scala.concurrent.Future

/**
  * Enable and order filters as you prefer them.
  * By default CORSFilter allows all origins. Make sure to configure it properly in application config.
  */

class Filters @Inject() (corsFilter: CORSFilter, securityHeadersFilter: SecurityHeadersFilter, setCookieFilter: SetCookieFilter) extends HttpFilters {
  def filters = Seq(corsFilter)
}

class SetCookieFilter @Inject()(override implicit val mat: Materializer) extends Filter {
  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    f(rh)//.map(_.withHeaders("Access-Control-Allow-Origin" -> "*"))
  }
}

