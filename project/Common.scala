import play.routes.compiler.InjectedRoutesGenerator
import sbt._
import sbt.Keys._
import play.sbt.routes._

object Common {
  val organisationString = "spring"
  val scalaVersionString = "2.11.8"
  val springRepo = "https://nexus.prod.corp/content"

  lazy val allResolvers = Seq(
    "spring" at s"$springRepo/groups/public",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Atlassian Releases" at "https://maven.atlassian.com/public/"
  )

  lazy val commonSettings = Seq(
    organization := organisationString,
    scalaVersion := scalaVersionString,
    scalacOptions := Seq(
      "-Xlint",
      //"-Xlog-implicits",
      "-deprecation",
      "-feature",
      "-encoding", "UTF-8",
      "-unchecked",
      "-Ywarn-dead-code",
      //"-Ywarn-value-discard",
      "-Ywarn-numeric-widen"
      ),
    resolvers ++= allResolvers,
    parallelExecution := false
  )


  lazy val playSettings = Seq(
    RoutesKeys.routesGenerator := InjectedRoutesGenerator)
}

