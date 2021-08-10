/*
 * Copyright 2021 HM Revenue & Customs
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

package it.uk.gov.hmrc.apisimulator

import akka.stream.Materializer
import controllers.AssetsMetadata
import org.scalatest.concurrent.ScalaFutures
import play.api.http.LazyHttpErrorHandler
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import play.api.{Application, Mode}
import play.mvc.Http.Status.OK
import uk.gov.hmrc.apisimulator.controllers.Documentation
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * To expose API's to Third Party Developers, the service needs to define the APIs in a definition.json and make it available under api/definition GET endpoint.
  * For all of the endpoints defined in the definition.json a documentation.xml needs to be provided and be available under api/documentation/[version]/[endpoint name] GET endpoint.
  * Example: api/documentation/1.0/Fetch-Some-Data
  *
  * See: "API Platform Architecture with Flows" on Confluence.
  */
class PlatformIntegrationSpec extends UnitSpec with ScalaFutures with GuiceOneServerPerSuite with StubControllerComponentsFactory {

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure("run.mode" -> "Stub")
    .configure(Map(
      "appName" -> "application-name",
      "appUrl" -> "http://microservice-name.example.com"))
    .in(Mode.Test).build()

  trait Setup {
    implicit val mat: Materializer = app.materializer
    val meta = app.injector.instanceOf[AssetsMetadata]
    val documentationController = new Documentation(LazyHttpErrorHandler, stubControllerComponents(), meta) {}
    val request = FakeRequest()
  }

  "microservice" should {
    "provide definition endpoint" in new Setup {
      val result = documentationController.definition()(request)
      status(result) shouldBe OK
    }

    "provide RAML conf endpoint and RAML for each version" in new Setup {
      val definitionResult: Result = await(documentationController.definition()(request))
      val definitionResponse: JsValue = jsonBodyOf(definitionResult)
      val versions: Seq[String] = (definitionResponse \\ "version") map (_.as[String])

      versions.foreach { version =>
        val result = documentationController.conf(version, "application.raml")(request)
        status(result) shouldBe OK
      }
    }
  }
}
