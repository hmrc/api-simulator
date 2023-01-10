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

package uk.gov.hmrc.apisimulator

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.Unauthorized
import uk.gov.hmrc.auth.core.AuthorisationException

import uk.gov.hmrc.apisimulator.config.{Binders, SaUtrBinder}

package object controllers {

  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }
  implicit val ninoBinder          = Binders

  implicit val utrBinder = SaUtrBinder

  def recovery: PartialFunction[Throwable, Result] = {
    case e: AuthorisationException => Unauthorized(Json.toJson(ErrorUnauthorized))
  }
}
