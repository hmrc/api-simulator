import _root_.play.sbt.PlayScala
import sbt.Keys._
import sbt._
import uk.gov.hmrc.DefaultBuildSettings._

lazy val appName = "api-simulator"

scalaVersion := "2.13.12"

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val IntegrationTest = config("it") extend Test
lazy val ComponentTest = config("component") extend Test
val testConfig = Seq(ComponentTest, IntegrationTest, Test)

lazy val playSettings: Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.apisimulator.controllers._", "uk.gov.hmrc.domain._"))

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(scalafixConfigSettings(IntegrationTest))
  .configs(testConfig: _*)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies(),

    Test / parallelExecution  := false,
    Test / fork := false,
    retrieveManaged := true,
    scalacOptions += "-Wconf:src=routes/.*:s"
  )
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"
  )
  .settings(
    unitTestSettings,
    integrationTestSettings,
    componentTestSettings)
  .settings(ScoverageSettings())
  .settings(
    scalacOptions ++= Seq(
      "-Wconf:cat=unused&src=views/.*\\.scala:s",
      "-Wconf:cat=unused&src=.*RoutesPrefix\\.scala:s",
      "-Wconf:cat=unused&src=.*Routes\\.scala:s",
      "-Wconf:cat=unused&src=.*ReverseRoutes\\.scala:s"
    )
  )

lazy val unitTestSettings =
  inConfig(Test)(Defaults.testTasks) ++
    Seq(
      Test / testOptions := Seq(Tests.Filter(onPackageName("unit"))),
      Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      Test / unmanagedSourceDirectories := Seq((Test / baseDirectory).value / "test"),
      addTestReportOption(Test, "test-reports")
    )

lazy val integrationTestSettings =
  inConfig(IntegrationTest)(Defaults.testTasks) ++
    Seq(
      IntegrationTest / testOptions := Seq(Tests.Filter(onPackageName("it"))),
      IntegrationTest / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      IntegrationTest / fork := false,
      IntegrationTest / parallelExecution  := false,
      addTestReportOption(IntegrationTest, "it-reports"))

lazy val componentTestSettings =
  inConfig(ComponentTest)(Defaults.testTasks) ++
    Seq(
      ComponentTest / testOptions := Seq(Tests.Filter(onPackageName("component"))),
      ComponentTest / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      ComponentTest / fork := false,
      ComponentTest / parallelExecution := false,
      addTestReportOption(ComponentTest, "component-reports")
    )

def onPackageName(rootPackage: String): String => Boolean = {
  testName => testName startsWith rootPackage
}

commands ++= Seq(
  Command.command("run-all-tests") { state => "test" :: "it:test" :: "component:test" :: state },

  Command.command("clean-and-test") { state => "clean" :: "compile" :: "run-all-tests" :: state },

  // Coverage does not need compile !
  Command.command("pre-commit") { state => "clean" :: "scalafmtAll" :: "scalafixAll" :: "coverage" :: "run-all-tests" :: "coverageOff" :: "coverageAggregate" :: state }
)
