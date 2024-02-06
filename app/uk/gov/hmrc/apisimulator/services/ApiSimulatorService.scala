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

package uk.gov.hmrc.apisimulator.services

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import com.google.inject.Singleton
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern

import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apisimulator.domain.Hello
import uk.gov.hmrc.apisimulator.util.StringUtils

trait ApiSimulatorService {
  def userApiWithLatency(latency: Int)(implicit hc: HeaderCarrier): Future[Hello]

  def userApiWithData(data: Int)(implicit hc: HeaderCarrier): Future[Hello]

  def world(implicit hc: HeaderCarrier): Future[Hello]

  def application(implicit hc: HeaderCarrier): Future[Hello]

  def user(implicit hc: HeaderCarrier): Future[Hello]
}

@Singleton
class LiveService @Inject() (system: ActorSystem)(implicit val ec: ExecutionContext) extends ApiSimulatorService {
  import scala.concurrent.duration._

  override def userApiWithLatency(latencyInMs: Int)(implicit hc: HeaderCarrier): Future[Hello] = {
    val delay: FiniteDuration = latencyInMs.millis
    pattern.after[Hello](delay, system.scheduler)(Future.successful(Hello("Hello User")))
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

@Singleton
class SandboxService extends ApiSimulatorService {

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
