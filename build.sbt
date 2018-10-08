import _root_.play.core.PlayVersion
import _root_.play.routes.compiler.StaticRoutesGenerator
import _root_.play.sbt.PlayImport._
import _root_.play.sbt.PlayScala
import _root_.play.sbt.routes.RoutesKeys.routesGenerator
import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val appName = "api-simulator"
lazy val appDependencies: Seq[ModuleID] = compile ++ test

lazy val compile = Seq(
  ws,
  "org.apache.commons" % "commons-io" % "1.3.2",
  "uk.gov.hmrc" %% "microservice-bootstrap" % "6.18.0",
  "uk.gov.hmrc" %% "domain" % "5.1.0"
)

lazy val test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % "test,it",
  "org.scalaj" %% "scalaj-http" % "2.3.0" % "test,it",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test,it",
  "org.pegdown" % "pegdown" % "1.6.0" % "test,it",
  "com.typesafe.play" %% "play-test" % PlayVersion.current % "test,it",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % "test,it",
  "org.mockito" % "mockito-core" % "2.12.0" % "test,it",
  "com.github.tomakehurst" % "wiremock" % "2.11.0" % "test,it",
  "info.cukes" %% "cucumber-scala" % "1.2.5" % "test,it",
  "info.cukes" % "cucumber-junit" % "1.2.5" % "test,it"
)

lazy val plugins: Seq[Plugins] = Seq(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
lazy val playSettings: Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.apisimulator.controllers._", "uk.gov.hmrc.domain._"))

lazy val microservice = Project(appName, file("."))
  .enablePlugins(plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    targetJvm := "jvm-1.8",
    scalaVersion := "2.11.11",
    majorVersion := 0,
    libraryDependencies ++= appDependencies,
    parallelExecution in Test := false,
    fork in Test := false,
    retrieveManaged := true,
    routesGenerator := StaticRoutesGenerator
  )
  .settings(
    testOptions in Test := Seq(Tests.Filter(_ => true)),// this removes duplicated lines in HTML reports
    unmanagedSourceDirectories in Test := Seq(baseDirectory.value / "test" / "unit"),
    addTestReportOption(Test, "test-reports")
  )
  .settings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := Seq(baseDirectory.value / "test" / "it" ),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(
    resolvers += Resolver.bintrayRepo("hmrc", "releases"),
    resolvers += Resolver.jcenterRepo)
  .settings(ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  })

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
  tests map {
    test => Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }

// Coverage configuration
coverageMinimum := 20
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
