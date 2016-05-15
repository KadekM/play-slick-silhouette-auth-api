package spring.bar.api.utils

import auth.core.util.CookieAuthFilter
import com.google.inject.Inject
import play.api.http.HttpFilters
import play.api.mvc.Filter
import play.filters.cors.CORSFilter
import play.filters.headers.SecurityHeadersFilter

/**
  * Enable and order filters as you prefer them.
  * By default CORSFilter allows all origins. Make sure to configure it properly in application config.
  */
class Filters @Inject()(corsFilter: CORSFilter,
                        securityHeadersFilter: SecurityHeadersFilter,
                        cookieAuthFilter: CookieAuthFilter)
    extends HttpFilters {
  def filters: Seq[Filter] = Seq(corsFilter, cookieAuthFilter)
}
