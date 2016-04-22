name := "play-slick-silhouette-auth-api"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.8",
  resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
)


lazy val authApiInterface = project.in(file("auth-api-interface"))
  .settings(commonSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "com.mohiva" %% "play-silhouette" % "4.0.0-BETA4",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA4",
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-BETA4",
      "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % Test,
      //"com.mohiva" %% "play-silhouette-persistence-memory" % "4.0.0-BETA4",
      "net.codingwell" %% "scala-guice" % "4.0.1",
      //"com.typesafe.slick" %% "slick" % "3.1.1",
      "com.h2database" % "h2" % "1.4.191",
      "com.typesafe.play" %% "play-slick" % "2.0.0",
      //"com.typesafe.play" %% "play-mailer" % "2.5.2",
      "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
      "org.postgresql" % "postgresql" % "9.4.1208.jre7",


      "com.github.tminglei" %% "slick-pg" % "0.12.2",
      "com.github.tminglei" %% "slick-pg_play-json" % "0.12.2",
      "com.github.tminglei" %% "slick-pg_date2" % "0.12.2",

      "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % "test"
    ))

lazy val authApi = project.in(file("auth-api"))
      .settings(commonSettings: _*)
      .settings(
 libraryDependencies ++= Seq(


)
      ).enablePlugins(PlayScala).dependsOn(authApiInterface)



lazy val client = (project in file("client"))
    .settings(commonSettings: _*)
    .enablePlugins(PlayScala).dependsOn(authApiInterface)
