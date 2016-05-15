package auth.http.util

import java.net.ConnectException

import com.google.inject.{Inject, Provider}
import play.api._
import play.api.http.{DefaultHttpErrorHandler, HttpErrorHandler}
import play.api.mvc.RequestHeader
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent.Future

/**
  * If there is problem with connect, return service unavailable
  */
class ConnectionErrorHandler @Inject()(env: Environment,
                                       config: Configuration,
                                       sourceMapper: OptionalSourceMapper,
                                       router: Provider[Router])
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  private val logger = Logger(this.getClass)

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] =
    exception match {
      case e: ConnectException ⇒
        logger.error(
            s"Connection failed for $request, transferring to ServiceUnavailable result", e)
        Future.successful(ServiceUnavailable(exception.getMessage))
      case e: Throwable ⇒ super.onServerError(request, exception)
    }
}
