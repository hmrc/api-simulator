import play.core.PlayVersion
import sbt._

object AppDependencies {

  lazy val bootstrapVersion = "7.12.0"

  def apply(): Seq[ModuleID] = compile ++ test

  lazy val compile = Seq(
    "uk.gov.hmrc"         %% "bootstrap-backend-play-28"  %  bootstrapVersion,
    "uk.gov.hmrc"         %% "domain"                           % "8.1.0-play-28",
    "commons-io"          %  "commons-io"                       % "2.11.0"
  )

  lazy val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"       % bootstrapVersion,
    "org.scalaj"              %% "scalaj-http"                  % "2.4.2",
    "org.mockito"             %% "mockito-scala-scalatest"      % "1.17.29",
    "org.scalatest"           %% "scalatest"                    % "3.2.17",
    "com.vladsch.flexmark"    %  "flexmark-all"                 % "0.62.2",
    "org.pegdown"             %  "pegdown"                      % "1.6.0",
    "com.github.tomakehurst"  %  "wiremock-jre8-standalone"     % "2.27.2",
    "io.cucumber"             %% "cucumber-scala"               % "5.7.0",
    "io.cucumber"             %  "cucumber-junit"               % "5.7.0"
  ).map(_ % "test, it")
}
