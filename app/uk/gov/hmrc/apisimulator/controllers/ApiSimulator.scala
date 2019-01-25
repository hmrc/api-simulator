/*
 * Copyright 2019 HM Revenue & Customs
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

import com.google.inject.Singleton
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BodyParsers}
import uk.gov.hmrc.apisimulator.services._
import uk.gov.hmrc.apisimulator.util.{BodyParsersUtils, TimeUtils}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ApiSimulator extends BaseController with HeaderValidator {

  def service: ApiSimulatorService
  implicit val hc: HeaderCarrier

  final def userApiWithLatency(latency: Int): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    service.userApiWithLatency(latency).map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def nino(nino: uk.gov.hmrc.domain.Nino): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    Logger.info(s"Request for nino $nino")
    Future(Ok(Json.toJson(Hello("Hello Nino"))))
  }

  final def utr(utr: uk.gov.hmrc.domain.SaUtr): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    Future(Ok(Json.toJson(Hello("Hello UTR"))))
  }

  final def userApiWithData(data: Int): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    service.userApiWithData(data).map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def world: Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Headers: " + request.headers)
    service.world.map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def application: Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Headers: " + request.headers)
    service.application.map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def user: Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Headers: " + request.headers)
    service.user.map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def post: Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    if (acceptHeaderValidationRules(request.headers.get(ACCEPT))) {
      Future(Ok(Json.toJson(Hello("Hello User"))))
    }
    else {
      Future.successful(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))
    }
  }

  final def postProcessBytes: Action[Long] = {
    val beginTime = System.currentTimeMillis()
    Action.async(BodyParsersUtils.bytesConsumer) { implicit request =>
      if (acceptHeaderValidationRules(request.headers.get(ACCEPT))) {
        val endTime = System.currentTimeMillis()
        val msg = s"Total of ${FileUtils.byteCountToDisplaySize(request.body)} read in ${TimeUtils.formatTimeDifference(beginTime, endTime)}"
        Logger.info(s"ApiSimulator#postProcessBytes(): $msg")
        Future(Ok(msg))
      }
      else {
        Future.successful(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))
      }
    }
  }

  final def put: Action[AnyContent] = Action.async { implicit request =>
    if (acceptHeaderValidationRules(request.headers.get(ACCEPT))) {
      Future(Ok(Json.toJson(Hello("Hello User"))))
    }
    else {
      Future.successful(Status(ErrorAcceptHeaderInvalid.httpStatusCode)(Json.toJson(ErrorAcceptHeaderInvalid)))
    }
  }

}

object SandboxController extends ApiSimulator {
  override val service: SandboxService.type = SandboxService
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}

@Singleton
class LiveController @Inject()(val service: LiveService) extends ApiSimulator {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}

@Singleton
class AuthLiveController @Inject() (val service: LiveService) extends ApiSimulator {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}

@Singleton
class IVLiveController @Inject()(val service: LiveService) extends ApiSimulator {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
