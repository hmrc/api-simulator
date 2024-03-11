/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.apisimulator.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import play.api.libs.json.Json

import uk.gov.hmrc.apisimulator.controllers._

class ErrorResponseSpec extends AnyWordSpec with Matchers {

  "errorResponse" should {
    "ErrorUnauthorized be translated to error Json with only the required fields" in {
      Json.toJson(ErrorUnauthorized).toString() shouldBe
        """{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized"}"""
    }

    "ErrorAcceptHeaderInvalid be translated to error Json with only the required fields" in {
      Json.toJson(ErrorAcceptHeaderInvalid).toString() shouldBe
        """{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}"""
    }

    "ErrorNotFound be translated to error Json with only the required fields" in {
      Json.toJson(ErrorNotFound).toString() shouldBe
        """{"code":"NOT_FOUND","message":"Resource was not found"}"""
    }

    "ErrorGenericBadRequest be translated to error Json with only the required fields" in {
      Json.toJson(ErrorGenericBadRequest).toString() shouldBe
        """{"code":"BAD_REQUEST","message":"Bad Request"}"""
    }

    "ErrorInternalServerError be translated to error Json with only the required fields" in {
      Json.toJson(ErrorInternalServerError).toString() shouldBe
        """{"code":"INTERNAL_SERVER_ERROR","message":"Internal server error"}"""
    }
  }

}
