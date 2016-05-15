import play.sbt.PlayImport
import sbt._

object Dependencies {

  private val acyclicVersion = "0.1.3"
  object CompilerDependencies {
    val acyclic = "com.lihaoyi" %% "acyclic" % acyclicVersion
  }

  private val slickVersion = "3.1.1"
  private val playSlickVersion = "2.0.0"
  private val scalacticVersion = "2.2.6"
  private val h2DbVersion = "1.4.191"
  private val postgresDbVersion = "9.4.1208"
  private val silhouetteVersion = "4.0.0-BETA4"
  private val scalaGuiceVersion = "4.0.1"
  private val slickPgVersion = "0.12.2"

  private val scalaTestVersion = "2.2.6"
  private val scalaTestPlusPlayVersion = "1.5.0"
  private val mockitoVersion = "2.0.44-beta"
  private val scalaCheckVersion = "1.12.5"
  private val scalazVersion = "7.2.2"
  private val shapelessVersion = "2.3.1"

  object RuntimeDependencies {
    val slick = "com.typesafe.slick" %% "slick" % slickVersion % Compile
    val scalactic = "org.scalactic" %% "scalactic" % scalacticVersion % Compile
    val scalaz = "org.scalaz" %% "scalaz-core" % scalazVersion % Compile
    val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion % Compile

    val h2Db = "com.h2database" % "h2" % h2DbVersion % Compile
    val postgresDb = "org.postgresql" % "postgresql" % postgresDbVersion % Compile

    val silhouette = "com.mohiva" %% "play-silhouette" % silhouetteVersion % Compile
    val silhouettePasswordBcrypt = "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion % Compile
    val silhouettePersistence = "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion % Compile
    val scalaGuice = "net.codingwell" %% "scala-guice" % scalaGuiceVersion % Compile
    val playSlick = "com.typesafe.play" %% "play-slick" % playSlickVersion % Compile
    val playSlickEvolution = "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion % Compile

    val slickPg = "com.github.tminglei" %% "slick-pg" % slickPgVersion % Compile
    val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % slickPgVersion % Compile
    val slickPgDate2 = "com.github.tminglei" %% "slick-pg_date2" % slickPgVersion % Compile
  }

  object TestDependencies {
    val silhouetteTestkit = "com.mohiva" %% "play-silhouette-testkit" % silhouetteVersion % Test
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % Test
    val mockito = "org.mockito" % "mockito-core" % mockitoVersion % Test
    val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test
  }

  val authCoreDependencies = Seq(
    RuntimeDependencies.silhouette,
    RuntimeDependencies.scalaGuice,
    TestDependencies.scalaCheck)

  val authDirectDependencies = Seq(
    RuntimeDependencies.silhouettePasswordBcrypt,
    RuntimeDependencies.silhouettePersistence,
    TestDependencies.silhouetteTestkit,

    RuntimeDependencies.playSlick,

    RuntimeDependencies.postgresDb,
    RuntimeDependencies.slickPg,
    RuntimeDependencies.slickPgPlayJson,
    RuntimeDependencies.slickPgDate2
  )

  val authHttpDependencies = Seq(
    RuntimeDependencies.scalaGuice,
    PlayImport.filters
  )

  // Inherits all form core as transitive dependencies
  val authApiDependencies = Seq(
    RuntimeDependencies.scalaz,
    RuntimeDependencies.playSlickEvolution,
    TestDependencies.scalaTestPlusPlay,
    TestDependencies.mockito,
    PlayImport.filters)

  val barApiDependencies = Seq(
    RuntimeDependencies.playSlickEvolution,
    TestDependencies.scalaTestPlusPlay,
    TestDependencies.mockito,
    PlayImport.filters)
}

