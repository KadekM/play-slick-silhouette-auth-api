name := "play-slick-silhouette-auth-api"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= Seq(
    "com.mohiva" %% "play-silhouette" % "4.0.0-BETA4",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-BETA4",
    "com.mohiva" %% "play-silhouette-persistence-memory" % "4.0.0-BETA4",

    "com.typesafe.slick" %% "slick" % "3.1.1",

    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-BETA4" % "test"
)


enablePlugins(PlayScala)