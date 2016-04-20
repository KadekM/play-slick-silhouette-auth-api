name := "play-slick-silhouette-auth-api"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= Seq(
    "com.mohiva" %% "play-silhouette" % "4.0.0-BETA4",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA4",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-BETA4",
    //"com.mohiva" %% "play-silhouette-persistence-memory" % "4.0.0-BETA4",

    "net.codingwell" %% "scala-guice" % "4.0.1",

    //"com.typesafe.slick" %% "slick" % "3.1.1",
    "com.h2database" % "h2" % "1.4.191",
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",

    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % "test"
)


enablePlugins(PlayScala)