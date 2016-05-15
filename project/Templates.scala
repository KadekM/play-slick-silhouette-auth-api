import Common._
import sbt.Keys._
import Dependencies._
import play.sbt.PlayScala
import CodeCorrectness._
import com.typesafe.sbt.packager.NativePackagerKeys
import com.typesafe.sbt.packager.universal.{UniversalKeys, UniversalPlugin}
import sbtbuildinfo._
import UniversalPlugin.autoImport._
import com.typesafe.sbt.SbtGit.git

object Templates extends BuildInfoKeys with UniversalKeys with NativePackagerKeys {

  def makeLibrary(project: sbt.Project): sbt.Project = project
    .settings(commonSettings: _*)
    // Checks:
    .configure(checkWarts)
    .configure(checkCyclicDependencies)
    .configure(withBuildInfo)
    // Release process:
    .configure(Release.Library.setupRelease)

  def makeRestApi(project: sbt.Project): sbt.Project = project
    .settings(commonSettings: _*)
    .settings(playSettings)
    // Checks:
    .configure(publishInTopFolder)
    .configure(checkWartsPlay)
    .configure(checkCyclicDependencies)
    .configure(withBuildInfo)
    .enablePlugins(PlayScala)
    // Release process:
    .configure(Release.RestApi.setupRelease)

  // Plugins
  def withBuildInfo(project: sbt.Project): sbt.Project = project
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoPackage := project.id.replace('-', '.'), // is set to package name so we can't use -
      buildInfoOptions += BuildInfoOption.ToMap,
       buildInfoKeys := Seq[BuildInfoKey](
         name,
         version,
         scalaVersion,
         sbtVersion,
         BuildInfoKey.action("buildTime") {System.currentTimeMillis},
         git.baseVersion,
         git.gitHeadCommit,
         git.formattedShaVersion
       )
      )

  def withDatabase(project: sbt.Project): sbt.Project = project
    // Dependencies:
    .settings(libraryDependencies += RuntimeDependencies.slick)

  private def publishInTopFolder(project: sbt.Project): sbt.Project = project
    .settings(
      topLevelDirectory := None,
      packageName in Universal := packageName.value
    )
}
