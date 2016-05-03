name := "play-slick-silhouette-auth-api"

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.8",
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
)


lazy val authCore = project.in(file("modules/auth-core"))
  .settings(commonSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "com.mohiva" %% "play-silhouette" % "4.0.0-BETA4" % Compile,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA4" % Compile,
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-BETA4" % Compile,
      "net.codingwell" %% "scala-guice" % "4.0.1" % Compile,
      "org.postgresql" % "postgresql" % "9.4.1208.jre7" % Compile,
      "com.typesafe.play" %% "play-slick" % "2.0.0" % Compile,

      "com.github.tminglei" %% "slick-pg" % "0.12.2" % Compile,
      "com.github.tminglei" %% "slick-pg_play-json" % "0.12.2" % Compile,
      "com.github.tminglei" %% "slick-pg_date2" % "0.12.2" % Compile,

      "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
    ))

lazy val authApi = project.in(file("modules/auth-api"))
      .settings(commonSettings: _*)
        .settings(libraryDependencies ++= Seq(
          "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0" % Compile,
          "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test,
          "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % Test,
          "org.mockito" % "mockito-core" % "2.0.52-beta" % Test,
            filters
        ))
      .enablePlugins(PlayScala).dependsOn(authCore % "compile->compile;test->test")

lazy val someAuthClient = (project in file("modules/some-auth-client"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= Seq(
      filters
    ))
    .enablePlugins(PlayScala).dependsOn(authCore % "compile->compile;test->test")
