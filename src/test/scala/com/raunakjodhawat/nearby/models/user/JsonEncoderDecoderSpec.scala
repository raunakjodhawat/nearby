package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._
import zio.Scope
import zio.json._
import zio.json.ast.Json
import zio.test.{Spec, TestEnvironment}
import zio.test._
import zio.test.junit.JUnitRunnableSpec

import java.util.Date

object JsonEncoderDecoderSpec {
  val dateLong: Long = 1690788936
  val date: Date = new Date(dateLong)
  val dateEncodeJSON: CharSequence = dateEncoder.encodeJson(date)
  val dateDecodeJSON: Either[String, Date] = dateDecoder.decodeJson("\"" + dateEncodeJSON + "\"")

  val fakeUserLocation: UserLocation = UserLocation(100, 100)
  val userLocationString: String =
    """
      |{
      |"lat": 100,
      |"long": 100
      |}
      |""".trim.stripMargin
  val userLocationEncodeJSON: CharSequence = userLocationEncoder.encodeJson(fakeUserLocation)
  val userLocationDecodeJSON: Either[String, UserLocation] =
    userLocationDecoder
      .decodeJson("{\"lat\": 100,\"long\": 100}")
}
class JsonEncoderDecoderSpec extends JUnitRunnableSpec {
  import JsonEncoderDecoderSpec._
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Json Encoder Decoder")(
    suite("Java Util Date")(
      test("encoding") {
        assert(dateEncodeJSON)(Assertion.equalTo(dateLong.toString))
      },
      test("decoding") {
        assert(dateDecodeJSON)(Assertion.equalTo(Right(date)))
      }
    ),
    suite("user location")(
      test("encoding") {
        println(userLocationEncodeJSON)
        assert(userLocationEncodeJSON)(Assertion.equalTo("""
                                                           |{"lat":100.0,"long":100.0}
                                                           |""".trim.stripMargin.replace("\"", """"""")))
      },
      test("decoding") {
        assert(userLocationDecodeJSON)(Assertion.equalTo(Right(fakeUserLocation)))
      }
    )
  )
}
