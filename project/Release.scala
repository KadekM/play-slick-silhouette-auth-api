import com.typesafe.sbt.packager.NativePackagerKeys
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin.autoImport._
import com.typesafe.sbt.packager.SettingsHelper._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal

//scalastyle: off
object Release extends UniversalKeys with NativePackagerKeys {

  object Library {
    def setupRelease(project: sbt.Project): sbt.Project = project
  }

  object RestApi {
    def setupRelease(project: sbt.Project): sbt.Project = project
      .enablePlugins(JavaAppPackaging)
      .settings(makeDeploymentSettings(Universal, packageBin in Universal, "zip")
      )
  }

  import sbtrelease._
  // we hide the existing definition for setReleaseVersion to replace it with our own
  import sbtrelease.ReleaseStateTransformations.{ setReleaseVersion ⇒ _, _ }

  private def setVersionOnly(selectVersion: Versions ⇒ String): ReleaseStep = { st: State ⇒
    val vs = st.get(ReleaseKeys.versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
    val selected = selectVersion(vs)

    st.log.info("Setting version to '%s'." format selected)
    val useGlobal = Project.extract(st).get(releaseUseGlobalVersion)
    val versionStr = (if (useGlobal) globalVersionString else versionString) format selected

    reapply(Seq(
      if (useGlobal) version in ThisBuild := selected
      else version := selected), st)
  }

  lazy val setReleaseVersionOnly: ReleaseStep = setVersionOnly(_._1)
}
