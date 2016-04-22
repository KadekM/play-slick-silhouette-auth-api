package auth.module

import auth.DefaultEnv
import auth.service.UserService
import com.google.inject.{ AbstractModule, Provides }
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{ Clock, IDGenerator, PasswordHasher, PasswordInfo }
import com.mohiva.play.silhouette.api.{ Environment, EventBus, Silhouette, SilhouetteProvider }
import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticatorSettings, JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

//todo:
import scala.concurrent.ExecutionContext.Implicits.global

sealed class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]

    bind[Clock].toInstance(Clock())
    bind[EventBus].toInstance(EventBus())
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())

    bind[ClassTag[JWTAuthenticator]].toInstance(implicitly[ClassTag[JWTAuthenticator]])
  }

  @Provides
  def provideEnvironment(userService: UserService,
    authenticatorService: AuthenticatorService[DefaultEnv#A],
    eventBus: EventBus): Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  @Provides
  def provideAuthenticatorService(configuration: Configuration,
    idGen: IDGenerator,
    clock: Clock): AuthenticatorService[JWTAuthenticator] = {
    val cfg = configuration.underlying
    val sharedSecret = cfg.getString("silhouette.authenticator.jwt.sharedSecret")
    val issuer = cfg.getString("silhouette.authenticator.jwt.issuerClaim")
    val expiry = cfg.getDuration("silhouette.authenticator.jwt.authenticatorExpiry")

    // we do not encrypt subject, as we do not transmit sensitive data AND it'd have to be decryptable across services
    val jwtSettings = JWTAuthenticatorSettings(
      encryptSubject = false,
      issuerClaim = issuer,
      authenticatorExpiry = Duration.fromNanos(expiry.toNanos),
      sharedSecret = sharedSecret)

    // Repository is set to `None`, meaning we utilize stateless JWT tokens - but we can't invalidate them
    // which can complicate logouts
    new JWTAuthenticatorService(
      settings = jwtSettings,
      repository = None,
      idGenerator = idGen,
      clock = clock)
  }

  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
    passwordHasher: PasswordHasher): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
}
