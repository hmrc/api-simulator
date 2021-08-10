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

package unit.uk.gov.hmrc.apisimulator.controllers

import java.util.UUID

import akka.stream.Materializer
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.Result
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import play.mvc.Http.Status.{OK, UNAUTHORIZED}
import uk.gov.hmrc.apisimulator.controllers.{AuthLiveController, IVLiveController}
import uk.gov.hmrc.apisimulator.services.LiveService
import uk.gov.hmrc.auth.core.retrieve.EmptyRetrieval
import uk.gov.hmrc.auth.core.{AuthConnector, InsufficientConfidenceLevel}
import uk.gov.hmrc.domain.{Generator, Nino, SaUtr}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.successful
import org.scalatestplus.mockito.MockitoSugar

class ApiSimulatorSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  trait Setup {
    implicit val mat: Materializer = fakeApplication.materializer
    val nino: Nino = new Generator().nextNino
    val utr: SaUtr = SaUtr(UUID.randomUUID.toString)
    val fakeRequest = FakeRequest().withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
  }

  trait AuthLiveSetup extends Setup with StubControllerComponentsFactory {
    val underTest = new AuthLiveController(mock[LiveService], mockAuthConnector, stubControllerComponents())
  }

  trait IVLiveSetup extends Setup with StubControllerComponentsFactory {
    val underTest = new IVLiveController(mock[LiveService], mockAuthConnector, stubControllerComponents())
  }

  "AuthLiveController" when {
    "calling the nino endpoint" should {
      "return a successful response" in new AuthLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(successful(()))

        val result: Result = await(underTest.nino(nino)(fakeRequest))

        status(result) shouldBe OK
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Hello Nino"
      }

      "return 401 if the user does not have a sufficient confidence level" in new AuthLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(Future.failed(InsufficientConfidenceLevel()))

        val result: Result = await(underTest.nino(nino)(fakeRequest))

        status(result) shouldBe UNAUTHORIZED
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Bearer token is missing or not authorized"
      }
    }

    "calling the utr endpoint" should {
      "return a successful response" in new AuthLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(successful(()))

        val result: Result = await(underTest.utr(utr)(fakeRequest))

        status(result) shouldBe OK
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Hello UTR"
      }

      "return 401 if the user does not have a sufficient confidence level" in new AuthLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(Future.failed(InsufficientConfidenceLevel()))

        val result: Result = await(underTest.utr(utr)(fakeRequest))

        status(result) shouldBe UNAUTHORIZED
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Bearer token is missing or not authorized"
      }
    }
  }

  "IVLiveController" when {
    "calling the nino endpoint" should {
      "return a successful response" in new IVLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(successful(()))

        val result: Result = await(underTest.nino(nino)(fakeRequest))

        status(result) shouldBe OK
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Hello Nino"
      }

      "return 401 if the user does not have a sufficient confidence level" in new IVLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(Future.failed(InsufficientConfidenceLevel()))

        val result: Result = await(underTest.nino(nino)(fakeRequest))

        status(result) shouldBe UNAUTHORIZED
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Bearer token is missing or not authorized"
      }
    }

    "calling the utr endpoint" should {
      "return a successful response" in new IVLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(successful(()))

        val result: Result = await(underTest.utr(utr)(fakeRequest))

        status(result) shouldBe OK
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Hello UTR"
      }

      "return 401 if the user does not have a sufficient confidence level" in new IVLiveSetup {
        when(mockAuthConnector.authorise(any(), ArgumentMatchers.eq(EmptyRetrieval))(any(), any())).thenReturn(Future.failed(InsufficientConfidenceLevel()))

        val result: Result = await(underTest.utr(utr)(fakeRequest))

        status(result) shouldBe UNAUTHORIZED
        (jsonBodyOf(result) \ "message").as[String] shouldBe "Bearer token is missing or not authorized"
      }
    }
  }
}
