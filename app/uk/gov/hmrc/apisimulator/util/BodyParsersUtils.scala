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

import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import org.reactivestreams.Subscriber
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.streams.IterateeStreams
import play.api.libs.streams.Accumulator
import play.api.mvc.BodyParser

import scala.concurrent.{ExecutionContext, Future}

trait BodyParsersUtils {

  implicit def ec: ExecutionContext

  val bytesConsumer: BodyParser[Long] = BodyParser { req =>
    val sink = Sink.fold(0L)( (u,t: ByteString) => u + t.length)
    Accumulator(sink).map(Right.apply)
//
//    val iteratee: Iteratee[Array[Byte], Long] =
//      Iteratee.fold[Array[Byte], Long](0) {
//        (length, bytes) => {
//          bytes.length + length
//        }
//      }
//    val subscriber: (Subscriber[Array[Byte]], Iteratee[Array[Byte], Long]) = IterateeStreams.iterateeToSubscriber(iteratee)
//    val result: Future[Long] = subscriber._2.run
//    val sink: Sink[Array[Byte], Future[Long]] = Sink.fromSubscriber(subscriber._1).mapMaterializedValue(_ => result)
//
//    Accumulator(sink).through[ByteString](Flow[ByteString].map(_.toArray))
//    .map(Right(_))
  }
}
