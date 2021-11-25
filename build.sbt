import _root_.play.core.PlayVersion
import _root_.play.sbt.PlayImport._
import _root_.play.sbt.PlayScala
import _root_.play.sbt.routes.RoutesKeys.routesGenerator
import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val appName = "api-simulator"
lazy val appDependencies: Seq[ModuleID] = compile ++ test
lazy val bootstrapVersion = "5.14.0"

lazy val compile = Seq(
  "uk.gov.hmrc"         %% "bootstrap-backend-play-28"  %  bootstrapVersion,

  "uk.gov.hmrc"         %% "domain"                           % "6.2.0-play-28",
  "com.typesafe.play"   %% "play-iteratees"                   % "2.6.1",
  "com.typesafe.play"   %% "play-iteratees-reactive-streams"  % "2.6.1"
)

lazy val test = Seq(
  "uk.gov.hmrc"             %% "bootstrap-test-play-28"       % bootstrapVersion,
  "org.scalaj"              %% "scalaj-http"                  % "2.3.0",
  "org.mockito"             %% "mockito-scala-scalatest"    % "1.14.8",
  "org.pegdown"             %  "pegdown"                      % "1.6.0",
  "com.github.tomakehurst"  %  "wiremock-jre8-standalone"     % "2.27.2",
  "info.cukes"              %% "cucumber-scala"               % "1.2.5",
  "info.cukes"              %  "cucumber-junit"               % "1.2.5"
).map(_ % "test, it")

lazy val IntegrationTest = config("it") extend Test
lazy val ComponentTest = config("component") extend Test
val testConfig = Seq(ComponentTest, IntegrationTest, Test)

lazy val plugins: Seq[Plugins] = Seq(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
lazy val playSettings: Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.apisimulator.controllers._", "uk.gov.hmrc.domain._"))

lazy val microservice = Project(appName, file("."))
  .enablePlugins(plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .configs(testConfig: _*)
  .settings(
    targetJvm := "jvm-1.8",
    scalaVersion := "2.12.12",
    majorVersion := 0,
    libraryDependencies ++= appDependencies,
    // dependencyOverrides ++= jettyOverrides,

    parallelExecution in Test := false,
    fork in Test := false,
    retrieveManaged := true
  )
  .settings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .settings(
    unitTestSettings,
    integrationTestSettings,
    componentTestSettings)

lazy val unitTestSettings =
  inConfig(Test)(Defaults.testTasks) ++
    Seq(
      testOptions in Test := Seq(Tests.Filter(onPackageName("unit"))),
      testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      unmanagedSourceDirectories in Test := Seq((baseDirectory in Test).value / "test"),
      addTestReportOption(Test, "test-reports")
    )

lazy val integrationTestSettings =
  inConfig(IntegrationTest)(Defaults.testTasks) ++
    Seq(
      testOptions in IntegrationTest := Seq(Tests.Filter(onPackageName("it"))),
      testOptions in IntegrationTest += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      fork in IntegrationTest := false,
      parallelExecution in IntegrationTest := false,
      addTestReportOption(IntegrationTest, "it-reports"))

lazy val componentTestSettings =
  inConfig(ComponentTest)(Defaults.testTasks) ++
    Seq(
      testOptions in ComponentTest := Seq(Tests.Filter(onPackageName("component"))),
      testOptions in ComponentTest += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
      fork in ComponentTest := false,
      parallelExecution in ComponentTest := false,
      addTestReportOption(ComponentTest, "component-reports")
    )

def onPackageName(rootPackage: String): String => Boolean = {
  testName => testName startsWith rootPackage
}

// Coverage configuration
coverageMinimum := 20
coverageFailOnMinimum := true
coverageExcludedPackages := """<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*"""



