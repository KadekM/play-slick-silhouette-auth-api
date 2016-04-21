package persistence

import com.google.inject.{AbstractModule, Inject, Provides}
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import persistence.dao.{LoginInfoDao, PasswordInfoDao, UserDao}
import persistence.dao.impl.{LoginInfoDaoImpl, PasswordInfoDaoImpl, UserDaoImpl}
import persistence.drivers.AuthPostgresDriver
import play.api.db.slick.DatabaseConfigProvider
import service.impl._
import slick.backend.DatabaseConfig
import slick.dbio.Effect.Schema
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

sealed class PersistenceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[UserDao].to[UserDaoImpl]
    bind[LoginInfoDao].to[LoginInfoDaoImpl]
    bind[PasswordInfoDao].to[PasswordInfoDaoImpl]
    bind[InitInMemoryDb].asEagerSingleton // only for in memory db, create tables at start

    // For silhouette
    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDaoImpl]
  }

  // todo: better stuff to inject
  @Provides
  def provideDbConfigForDefaultJdbc(dbConfigProvider: DatabaseConfigProvider): DatabaseConfig[JdbcProfile] =
    dbConfigProvider.get[JdbcProfile]

  @Provides
  def provideDbConfigForAuthPostgresDriver(dbConfigProvider: DatabaseConfigProvider): DatabaseConfig[AuthPostgresDriver] =
    dbConfigProvider.get[AuthPostgresDriver]
}

// TODO: dependant path to package obj
class InitInMemoryDb @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) {
  import dbConfig.driver.api._
  import play.api.libs.concurrent.Execution.Implicits._

  private val f: DBIOAction[Unit, NoStream, Schema] = for {
    _ ← UserTable.query.schema.create
    _ ← LoginInfoTable.query.schema.create
    _ ← PasswordInfoTable.query.schema.create
  } yield ()

  Await.ready(dbConfig.db.run(f.transactionally), 10.seconds)
  println("Tables created")
}
