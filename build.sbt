import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings._

lazy val appName = "api-simulator"

Global / bloopAggregateSourceDependencies := true
Global / bloopExportJarClassifiers := Some(Set("sources"))

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / majorVersion := 0
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    scalacOptions += "-Wconf:src=routes/.*:s"
  )
  .settings(
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"
  )
  .settings(ScoverageSettings())
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.apisimulator.controllers._",
      "uk.gov.hmrc.domain._"
    )
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    name := "integration-tests",
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    DefaultBuildSettings.itSettings(),
    addTestReportOption(Test, "int-test-reports")
  )

lazy val component = (project in file("component"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    name := "component-tests",
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    DefaultBuildSettings.itSettings(),
    addTestReportOption(Test, "component-reports")
  )


commands ++= Seq(
  Command.command("cleanAll") { state => "clean" :: "it/clean" :: "component/clean" :: state },
  Command.command("fmtAll") { state => "scalafmtAll" :: "it/scalafmtAll" :: "component/scalafmtAll" :: state },
  Command.command("fixAll") { state => "scalafixAll" :: "it/scalafixAll" :: "component/scalafixAll" :: state },
  Command.command("testAll") { state => "test" :: "it/test" :: "component/test" :: state },

  Command.command("run-all-tests") { state => "testAll" :: state },
  Command.command("clean-and-test") { state => "cleanAll" :: "compile" :: "run-all-tests" :: state },
  Command.command("pre-commit") { state => "cleanAll" :: "fmtAll" :: "fixAll" :: "coverage" :: "testAll" :: "coverageOff" :: "coverageAggregate" :: state }
)
