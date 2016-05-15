import Common._

import Templates._
import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, _}
import sbtrelease.ReleaseStateTransformations._

name := "auth-api"
scalaVersion := scalaVersionString
organization := organisationString
scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
parallelExecution in ThisBuild := false

// Projects
lazy val authCore = Project(id = "auth-core", base = file("modules/auth/auth-core"))
  .configure(makeLibrary)
  .settings(libraryDependencies ++= Dependencies.authCoreDependencies)

lazy val authDirect = Project(id = "auth-direct", base = file("modules/auth/auth-direct"))
  .configure(makeLibrary)
  .dependsOn(authCore % "compile->compile;test->test")
  .settings(libraryDependencies ++= Dependencies.authDirectDependencies)

lazy val authApi = Project(id = "auth-api", base = file("modules/auth/auth-api"))
    .configure(makeRestApi)
    .dependsOn(authDirect % "compile->compile;test->test")
    .settings(libraryDependencies ++= Dependencies.authApiDependencies)

lazy val authHttp = Project(id = "auth-http", base = file("modules/auth/auth-http"))
  .configure(makeLibrary)
  .dependsOn(authCore % "compile->compile;test->test")
  .settings(libraryDependencies ++= Dependencies.authHttpDependencies)

lazy val barApi = Project(id = "bar-api", base = file("modules/bar/bar-api"))
  .configure(makeRestApi)
  .dependsOn(authHttp % "compile->compile;test->test")
  .settings(libraryDependencies ++= Dependencies.barApiDependencies)

lazy val root = Project(id = "root", base = file("."))
	.aggregate(authDirect, authHttp, authCore, authApi, barApi)
  .enablePlugins(UniversalPlugin)
  .settings(
    publishArtifact := false, // root itself is not artifact, only subprojects

    releaseVersionBump := sbtrelease.Version.Bump.Bugfix,

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      releaseStepTask(publish in Universal in barApi),
      releaseStepTask(publish in Universal in authApi),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )

enablePlugins(GitBranchPrompt)
