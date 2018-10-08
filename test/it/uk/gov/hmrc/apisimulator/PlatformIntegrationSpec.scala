/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apisimulator

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import uk.gov.hmrc.apisimulator.domain.Registration
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, TestData}
import org.scalatestplus.play.OneAppPerTest
import play.api.http.LazyHttpErrorHandler
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.{Application, Mode}
import play.mvc.Http.Status.{NO_CONTENT, OK}
import uk.gov.hmrc.apisimulator.controllers.Documentation
import uk.gov.hmrc.play.microservice.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.test.UnitSpec

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * 1, To integrate with API platform the service needs to register itself to the service locator by calling the /registration endpoint and providing
  * - application name
  * - application url
  *
  * 2a, To expose API's to Third Party Developers, the service needs to define the APIs in a definition.json and make it available under api/definition GET endpoint
  * 2b, For all of the endpoints defined in the definition.json a documentation.xml needs to be provided and be available under api/documentation/[version]/[endpoint name] GET endpoint
  * Example: api/documentation/1.0/Fetch-Some-Data
  *
  * See: "API Platform Architecture with Flows" on Confluence.
  */
class PlatformIntegrationSpec extends UnitSpec with MockitoSugar with ScalaFutures with BeforeAndAfterEach with OneAppPerTest {

  val stubHost = "localhost"
  val stubPort = sys.env.getOrElse("WIREMOCK_SERVICE_LOCATOR_PORT", "11111").toInt
  val wireMockServer = new WireMockServer(wireMockConfig().port(stubPort))

  override def newAppForTest(testData: TestData): Application = GuiceApplicationBuilder()
    .configure("run.mode" -> "Stub")
    .configure(Map(
      "appName" -> "application-name",
      "appUrl" -> "http://microservice-name.example.com",
      "microservice.services.service-locator.host" -> stubHost,
      "microservice.services.service-locator.port" -> stubPort))
    .in(Mode.Test).build()

  override def beforeEach(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
    stubFor(post(urlMatching("/registration")).willReturn(aResponse().withStatus(NO_CONTENT)))
  }

  trait Setup extends MicroserviceFilterSupport {
    val documentationController = new Documentation(LazyHttpErrorHandler) {}
    val request = FakeRequest()
  }

  "microservice" should {

    "register itelf to service-locator" in new Setup {
      def regPayloadStringFor(serviceName: String, serviceUrl: String): String =
        Json.toJson(Registration(serviceName, serviceUrl, Some(Map("third-party-api" -> "true")))).toString

      verify(1, postRequestedFor(urlMatching("/registration")).
        withHeader("content-type", equalTo("application/json")).
        withRequestBody(equalTo(regPayloadStringFor("application-name", "http://microservice-name.example.com"))))
    }

    "provide definition endpoint" in new Setup {
      val result = documentationController.definition()(request)
      status(result) shouldBe OK
    }

    "provide RAML conf endpoint and RAML for each version" in new Setup {
      val definitionResult = documentationController.definition()(request)
      val definitionResponse = jsonBodyOf(definitionResult).futureValue
      val versions: Seq[String] = (definitionResponse \\ "version") map (_.as[String])

      versions.foreach { version =>
        val result = documentationController.conf(version, "application.raml")(request)
        status(result) shouldBe OK
      }
    }
  }

  override protected def afterEach(): Unit = {
    wireMockServer.stop()
    wireMockServer.resetMappings()
  }
}
