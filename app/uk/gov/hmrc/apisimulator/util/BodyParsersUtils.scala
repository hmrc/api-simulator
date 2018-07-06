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

package uk.gov.hmrc.apisimulator.util

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import play.api.libs.iteratee.Iteratee
import play.api.libs.streams.Streams
import play.api.mvc.BodyParser

object BodyParsersUtils {

  import scala.concurrent.ExecutionContext.Implicits.global

  val bytesConsumer = BodyParser { req =>
    val iteratee: Iteratee[Array[Byte], Long] =
      Iteratee.fold[Array[Byte], Long](0) {
        (length, bytes) => {
          bytes.length + length
        }
      }

    Streams.iterateeToAccumulator(iteratee)
      .through[ByteString](Flow[ByteString].map(_.toArray))
      .map(Right(_))
  }

}
