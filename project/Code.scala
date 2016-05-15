import sbt._
import sbt.Keys._
import wartremover._
import Dependencies.CompilerDependencies._

object CodeCorrectness {
    import wartremover.Wart._

  def checkWarts(project: sbt.Project): sbt.Project = project.settings(
    wartremoverWarnings in (Compile, compile) := Seq(
      Any2StringAdd,
      EitherProjectionPartial,
      Enumeration,
      FinalCaseClass,
      JavaConversions,
      NoNeedForMonad,
      Null,
      Option2Iterable,
      Product,
      Return,
      Serializable))

  def checkWartsPlay(project: sbt.Project): sbt.Project = project
    .configure(checkWarts)
    .settings(
      wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "Routes.scala",
      wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "RoutesPrefix.scala",
      wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "ReverseRoutes.scala",
      wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "javascript" / "JavaScriptReverseRoutes.scala")

  def checkCyclicDependencies(project: sbt.Project): sbt.Project = project
    .settings(
      libraryDependencies += acyclic % Provided,
      autoCompilerPlugins := true,
      addCompilerPlugin(acyclic))

}
/* broken auto-format?
object CodeStyle {
  def setScalariform(project: sbt.Project): sbt.Project = project.settings(
      SbtScalariform.defaultScalariformSettings ++ (
        ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignParameters, false)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
        .setPreference(CompactControlReadability, false)
        .setPreference(CompactStringConcatenation, false)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(FormatXml, true)
        .setPreference(IndentLocalDefs, false)
        .setPreference(IndentPackageBlocks, true)
        .setPreference(IndentSpaces, 2)
        .setPreference(IndentWithTabs, false)
        .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
        .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
        .setPreference(PreserveSpaceBeforeArguments, false)
        .setPreference(RewriteArrowSymbols, true)
        .setPreference(SpaceBeforeColon, false)
        .setPreference(SpaceInsideBrackets, false)))
}*/
