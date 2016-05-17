package auth.core.module

import akka.actor.ActorSystem
import auth.core._
import auth.core.service.BasicUserService
import auth.core.service.authorization.PermissionsAuthorizer
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, IDGenerator}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

sealed class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Clock].toInstance(Clock())
    bind[EventBus].toInstance(EventBus())
    bind[IDGenerator].toProvider[IDGeneratorProvider]

    bind[ExecutionContext] // Runtime binding of marked execution context
      .annotatedWith[SilhouetteContext]
      .toProvider[SilhouetteExecutionContextProvider]
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[ClassTag[JWTAuthenticator]].toInstance(implicitly[ClassTag[JWTAuthenticator]])
  }

  @Provides
  def provideAuthenticatorService(
      configuration: Configuration,
      idGen: IDGenerator,
      clock: Clock,
      @SilhouetteContext ec: ExecutionContext): AuthenticatorService[JWTAuthenticator] = {
    val cfg          = configuration.underlying
    val sharedSecret = cfg.getString("silhouette.authenticator.jwt.sharedSecret")
    val issuer       = cfg.getString("silhouette.authenticator.jwt.issuerClaim")
    val expiry       = cfg.getDuration("silhouette.authenticator.jwt.authenticatorExpiry")
    val fieldName    = cfg.getString("silhouette.authenticator.jwt.fieldName")

    // we do not encrypt subject, as we do not transmit sensitive data AND it'd have to be decryptable across services
    val jwtSettings = JWTAuthenticatorSettings(
        fieldName = fieldName,
        encryptSubject = false,
        issuerClaim = issuer,
        authenticatorExpiry = Duration.fromNanos(expiry.toNanos),
        sharedSecret = sharedSecret)

    // Repository is set to `None`, meaning we utilize stateless JWT tokens - but we can't invalidate them
    // which can complicate logouts
    new JWTAuthenticatorService(
        settings = jwtSettings, repository = None, idGenerator = idGen, clock = clock)(ec)
  }
}

private class IDGeneratorProvider @Inject()(@SilhouetteContext ec: ExecutionContext)
    extends Provider[IDGenerator] {
  val generator = new SecureRandomIDGenerator()(ec)
  override def get(): IDGenerator = generator
}

/** Injectable ExecutionContext */
private class SilhouetteExecutionContextProvider @Inject()(system: ActorSystem)
    extends Provider[ExecutionContext] {
  override def get(): ExecutionContext =
    system.dispatchers.lookup("silhouette.thread.context")
}
