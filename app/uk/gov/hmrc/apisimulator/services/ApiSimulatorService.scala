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

package uk.gov.hmrc.apisimulator.services

import akka.actor.ActorSystem
import com.google.inject.Singleton
import javax.inject.Inject
import play.api.libs.json.Json
import uk.gov.hmrc.apisimulator.util.StringUtils
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

case class Hello(message: String)

import scala.concurrent.Future

object Hello {
  implicit val format = Json.format[Hello]
}


trait ApiSimulatorService {
  def userApiWithLatency(latency: Int)(implicit hc: HeaderCarrier): Future[Hello]


  def userApiWithData(data: Int)(implicit hc: HeaderCarrier): Future[Hello]

  def world(implicit hc: HeaderCarrier): Future[Hello]

  def application(implicit hc: HeaderCarrier): Future[Hello]

  def user(implicit hc: HeaderCarrier): Future[Hello]
}

@Singleton
class LiveService @Inject() (system: ActorSystem) extends ApiSimulatorService {
  import scala.concurrent.duration._

  override def userApiWithLatency(latencyInMs: Int)(implicit hc: HeaderCarrier): Future[Hello] = {
    val delay: FiniteDuration = latencyInMs.millis
    akka.pattern.after[Hello](delay, system.scheduler)(Future.successful(Hello("Hello User")))
  }

  override def userApiWithData(data: Int)(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello(StringUtils.generateRandomString(data)))
  }

  override def world(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello World"))
  }

  override def application(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello Application"))
  }

  override def user(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello User"))
  }
}

object SandboxService extends ApiSimulatorService {
  override def userApiWithLatency(latencyInMs: Int)(implicit hc: HeaderCarrier): Future[Hello] = {
    Thread.sleep(latencyInMs)
    Future.successful(Hello("Hello Sandbox User"))
  }

  override def userApiWithData(data: Int)(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Sandbox " + StringUtils.generateRandomString(data)))
  }

  override def world(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello Sandbox World"))
  }

  override def application(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello Sandbox Application"))
  }

  override def user(implicit hc: HeaderCarrier): Future[Hello] = {
    Future.successful(Hello("Hello Sandbox User"))
  }
}
