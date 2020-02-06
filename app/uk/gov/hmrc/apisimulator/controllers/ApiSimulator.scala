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

package uk.gov.hmrc.apisimulator.controllers

import com.google.inject.Singleton
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BodyParsers, Result}
import uk.gov.hmrc.apisimulator.domain.Hello
import uk.gov.hmrc.apisimulator.services._
import uk.gov.hmrc.apisimulator.util.{BodyParsersUtils, TimeUtils}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, ConfidenceLevel}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}

trait ApiSimulator extends BaseController with HeaderValidator with BodyParsersUtils with AuthorisedFunctions {

  def service: ApiSimulatorService

  final def userApiWithLatency(latency: Int): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
    service.userApiWithLatency(latency).map(as => Ok(Json.toJson(as))
    ) recover {
      case _ => Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError))
    }
  }

  final def userApiWithData(data: Int): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
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

  def nino(nino: Nino): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    handleNino(nino)
  }

  final def handleNino(nino: Nino): Future[Result] = {
    Logger.info(s"Request for nino $nino")
    Future(Ok(Json.toJson(Hello("Hello Nino"))))
  }

  def utr(utr: uk.gov.hmrc.domain.SaUtr): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    handleUtr(utr)
  }

  final def handleUtr(utr: uk.gov.hmrc.domain.SaUtr) = {
    Future(Ok(Json.toJson(Hello("Hello UTR"))))
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
    Action.async(bytesConsumer) { implicit request =>
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

@Singleton
class SandboxController @Inject()(override val service: SandboxService, override val authConnector: AuthConnector)
                                 (implicit override val ec: ExecutionContext) extends ApiSimulator

@Singleton
class LiveController @Inject()(override val service: LiveService, override val authConnector: AuthConnector)
                              (implicit override val ec: ExecutionContext) extends ApiSimulator

@Singleton
class AuthLiveController @Inject() (override val service: LiveService, override val authConnector: AuthConnector)
                                   (implicit override val ec: ExecutionContext) extends ApiSimulator {

  final override def nino(nino: Nino): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
    authorised(ConfidenceLevel.L50) {
      handleNino(nino)
    } recover recovery
  }

  final override def utr(utr: uk.gov.hmrc.domain.SaUtr): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
    authorised(ConfidenceLevel.L50) {
      handleUtr(utr)
    } recover recovery
  }
}

@Singleton
class IVLiveController @Inject()(override val service: LiveService, override val authConnector: AuthConnector)
                                (implicit override val ec: ExecutionContext) extends ApiSimulator {

  final override def nino(nino: Nino): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
    authorised(ConfidenceLevel.L200) {
      handleNino(nino)
    } recover recovery
  }

  final override def utr(utr: uk.gov.hmrc.domain.SaUtr): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async { implicit request =>
    authorised(ConfidenceLevel.L200) {
      handleUtr(utr)
    } recover recovery
  }
}
