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
  "uk.gov.hmrc" %% "microservice-bootstrap" % "10.6.0",
  "uk.gov.hmrc" %% "domain" % "5.1.0"
)

lazy val test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.3.0" % "test,it",
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

lazy val IntegrationTest = config("it") extend Test
lazy val ComponentTest = config("component") extend Test
val testConfig = Seq(ComponentTest, IntegrationTest, Test)

lazy val plugins: Seq[Plugins] = Seq(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
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
    scalaVersion := "2.11.11",
    majorVersion := 0,
    libraryDependencies ++= appDependencies,
    parallelExecution in Test := false,
    fork in Test := false,
    retrieveManaged := true,
    routesGenerator := StaticRoutesGenerator
  )
  .settings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .settings(
    unitTestSettings,
    integrationTestSettings,
    componentTestSettings)
  .settings(
    resolvers += Resolver.bintrayRepo("hmrc", "releases"),
    resolvers += Resolver.jcenterRepo)
  .settings(ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  })

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

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
  tests map {
    test => Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }

// Coverage configuration
coverageMinimum := 20
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
