/*
 * Copyright 2019 HM Revenue & Customs
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

package component

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.{Application, Mode}
import uk.gov.hmrc.play.test.UnitSpec

class AppHealthSpec extends UnitSpec with GuiceOneServerPerSuite{


  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .in(Mode.Test).build()

  "the application" when {
    "the health check endpoint is called" should {
      "respond with 200 OK" in {
        val wsClient = app.injector.instanceOf[WSClient]
        val response = await(wsClient.url(s"http://localhost:$port/ping/ping").get)
        response.status shouldBe OK
      }
    }
  }
}
