package module

import com.google.inject.{AbstractModule, Inject, Provides}
import com.mohiva.play.silhouette.api.AuthInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import dao.UserDao
import dao.impl.{PasswordInfoDao, UserDaoImpl}
import mapping._
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import service.{Hasher, UserService, UserTokenService}
import service.impl._
import slick.backend.DatabaseConfig
import slick.dbio.Effect.Schema
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[UserService].to[UserServiceImpl]
    bind[UserTokenService].to[InMemoryUserTokenServiceImpl].asEagerSingleton

    //todo: to dao?
    bind[UserDao].to[UserDaoImpl]
    bind[InitInMemoryDb].asEagerSingleton // only for in memory db, create tables at start
  }


  // todo: better stuff to inject
  @Provides
  def provideDbConfig(dbConfigProvider: DatabaseConfigProvider): DatabaseConfig[JdbcProfile] =
    dbConfigProvider.get[JdbcProfile]
}

// TODO: dependant path to package obj
class InitInMemoryDb @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) {
  import dbConfig.driver.api._
  import play.api.libs.concurrent.Execution.Implicits._

  private val f: DBIOAction[Unit, NoStream, Schema] = for {
    _ ← UserTable.table.schema.create
    _ ← LoginInfoTable.table.schema.create
    _ ← UserToLoginInfoTable.table.schema.create
    _ ← PasswordInfoTable.table.schema.create
  } yield ()

  Await.ready(dbConfig.db.run(f.transactionally), 10.seconds)
  println("Tables created")
}

