import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "9.19.0"

  def apply(): Seq[ModuleID] = compile ++ test

  lazy val compile = Seq(
    "uk.gov.hmrc"         %% "bootstrap-backend-play-30"        %  bootstrapVersion,
    "uk.gov.hmrc"         %% "domain-play-30"                   % "9.0.0",
    "commons-io"          %  "commons-io"                       % "2.14.0"
  )

  lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"       % bootstrapVersion,
    "org.mockito"             %% "mockito-scala-scalatest"      % "2.0.0"
  ).map(_ % "test")
}
