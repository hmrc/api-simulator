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

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.Json
import play.api.mvc._

trait HeaderValidator extends Results {

  val validateVersion: String => Boolean = List("1.0", "2.0").contains

  val validateContentType: String => Boolean = _ == "json"

  val matchHeader: String => Option[Match] = new Regex("""^application/vnd\.hmrc\.(.*?)\+(.*)$""", "version", "contenttype") findFirstMatchIn _

  val acceptHeaderValidationRules: Option[String] => Boolean =
    _ flatMap (a => matchHeader(a) map (res => validateContentType(res.group("contenttype")) && validateVersion(res.group("version")))) getOrElse false

  def validateAccept(rules: Option[String] => Boolean)(implicit ec: ExecutionContext) = new ActionFilter[Request] {

    override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful {
      if (rules(request.headers.get(ACCEPT))) {
        None
      } else {
        Some(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))
      }
    }

    override protected def executionContext: ExecutionContext = ec

  }
}
