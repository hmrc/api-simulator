/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.apisimulator.util

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import play.api.libs.streams.Accumulator
import play.api.mvc.BodyParser

import scala.concurrent.ExecutionContext

trait BodyParsersUtils {

  implicit def ec: ExecutionContext

  val bytesConsumer: BodyParser[Long] = BodyParser { req =>
    val sink = Sink.fold(0L)((u, t: ByteString) => u + t.length)
    Accumulator(sink).map(Right.apply)
  }
}
