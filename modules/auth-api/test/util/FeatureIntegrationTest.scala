package util

import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Mode}

trait FeatureIntegrationTest extends PlaySpec with OneAppPerSuite with ScalaFutures with IntegrationPatience
    with BeforeAndAfterAll with PopulateDb with TestAuthDatabaseAccess with AuthSpecHelpers {

  override implicit lazy val app: Application = new GuiceApplicationBuilder().in(Mode.Test).build()
  lazy val config = app.injector.instanceOf[Configuration].underlying
  lazy val jwtHeaderFieldName = config.getString("silhouette.authenticator.jwt.fieldName")
  lazy val waitForDbDuration = config.getDuration("testkit.wait-for-db").getNano / 1000000

  def waitForDb() = Thread.sleep(waitForDbDuration)
}
