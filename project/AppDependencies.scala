import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "9.0.0"

  def apply(): Seq[ModuleID] = compile ++ test

  lazy val compile = Seq(
    "uk.gov.hmrc"         %% "bootstrap-backend-play-30"        %  bootstrapVersion,
    "uk.gov.hmrc"         %% "domain-play-30"                   % "9.0.0",
    "commons-io"          %  "commons-io"                       % "2.11.0"
  )

  lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"       % bootstrapVersion,
    "org.mockito"             %% "mockito-scala-scalatest"      % "1.17.30",
    "org.pegdown"             %  "pegdown"                      % "1.6.0",
    "io.cucumber"             %% "cucumber-scala"               % "8.14.1",
    "io.cucumber"             %  "cucumber-junit"               % "7.11.1"
  ).map(_ % "test")
}
