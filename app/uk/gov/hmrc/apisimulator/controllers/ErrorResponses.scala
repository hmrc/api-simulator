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

package uk.gov.hmrc.apisimulator.controllers

import play.api.libs.json.{JsObject, JsValue, Json, Writes}

sealed abstract class ErrorResponse(val httpStatusCode: Int,
                                    val errorCode: String,
                                    val message: String)
object ErrorResponse{
  def writes(error: ErrorResponse): JsObject = {
    Json.obj(
      "code" -> error.errorCode,
      "message" -> error.message
    )
  }
}


case object ErrorUnauthorized extends ErrorResponse(401, "UNAUTHORIZED", "Bearer token is missing or not authorized") {
  implicit val implicitwrites = new Writes[ErrorUnauthorized.type] {
    override def writes(error: ErrorUnauthorized.type): JsValue = {
      ErrorResponse.writes(error)
    }
  }
}

case object ErrorNotFound extends ErrorResponse(404, "NOT_FOUND", "Resource was not found"){
  implicit val implicitwrites = new Writes[ErrorNotFound.type] {
    override def writes(error: ErrorNotFound.type): JsValue = {
      ErrorResponse.writes(error)
    }
  }
}

case object ErrorGenericBadRequest extends ErrorResponse(400, "BAD_REQUEST", "Bad Request"){
  implicit val implicitwrites = new Writes[ErrorGenericBadRequest.type] {
    override def writes(error: ErrorGenericBadRequest.type): JsValue = {
      ErrorResponse.writes(error)
    }
  }
}

case object ErrorAcceptHeaderInvalid extends ErrorResponse(406, "ACCEPT_HEADER_INVALID", "The accept header is missing or invalid"){
  implicit val implicitwrites = new Writes[ErrorAcceptHeaderInvalid.type] {
    override def writes(error: ErrorAcceptHeaderInvalid.type): JsValue = {
      ErrorResponse.writes(error)
    }
  }
}

case object ErrorInternalServerError extends ErrorResponse(500, "INTERNAL_SERVER_ERROR", "Internal server error"){
  implicit val implicitwrites = new Writes[ErrorInternalServerError.type] {
    override def writes(error: ErrorInternalServerError.type): JsValue = {
      ErrorResponse.writes(error)
    }
  }
}

