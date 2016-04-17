package module

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.util.{PlayCacheLayer, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.CacheAuthenticatorRepository
import io.netty.channel.DefaultAddressedEnvelope
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.cache.{CacheApi, EhCacheApi}
import service.{UserService, UserServiceImpl}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag // TODO

class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[UserService].to[UserServiceImpl]

    bind[CacheLayer].to[PlayCacheLayer]
    bind[AuthenticatorRepository[JWTAuthenticator]].to[CacheAuthenticatorRepository[JWTAuthenticator]] // todo: swap for db one // todo fix hardcoded jwt?

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
  def provideAuthenticatorService(idGen: IDGenerator,
                                   repository: AuthenticatorRepository[JWTAuthenticator],
                                   clock: Clock): AuthenticatorService[JWTAuthenticator] = {
    //todo: load from config
    new JWTAuthenticatorService(
      settings = JWTAuthenticatorSettings(sharedSecret = "a34o5o5n43n34onio43diofgjodfggodngspodp"),
      repository = Some(repository),
      idGenerator = idGen,
      clock = clock
    )
  }
}
