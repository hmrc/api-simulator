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

package uk.gov.hmrc.apisimulator.util

import scala.util.Random

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import uk.gov.hmrc.apisimulator.controllers.HeaderValidator
import uk.gov.hmrc.apisimulator.util.StringUtils

class StringUtilsSpec extends AnyWordSpec with Matchers with HeaderValidator {

  // Note that this method is not efficient for large strings
  private def generateStringSlowly(numberOfChars: Int): String = {
    Random.alphanumeric.take(numberOfChars).mkString
  }

  "Generating random strings" should {
    "be very fast" in {

      val size: Int = Int.MaxValue / 1000

      // using Apache Commons IO (fast implementation)
      val time1 = System.currentTimeMillis()
      val str1  = StringUtils.generateRandomString(size)
      val time2 = System.currentTimeMillis()
      str1.length shouldBe size

      val execTime1 = time2 - time1

      // using Scala (slow implementation)
      val time3 = System.currentTimeMillis()
      val str2  = generateStringSlowly(size)
      val time4 = System.currentTimeMillis()
      str2.length shouldBe size

      val execTime2 = time4 - time3

      execTime1 shouldBe <(execTime2)
    }
  }
}
