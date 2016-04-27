package utils

import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject() (corsFilter: CORSFilter, securityHeadersFilter: SecurityHeadersFilter) extends HttpFilters {
  def filters = Seq(corsFilter)
}