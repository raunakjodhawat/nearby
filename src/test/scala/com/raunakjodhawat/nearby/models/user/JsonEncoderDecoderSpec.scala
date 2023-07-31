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
  val userLocationString: String = "{\"lat\":100.0,\"long\":100.0}"
  val userLocationEncodeJSON: CharSequence = userLocationEncoder.encodeJson(fakeUserLocation)
  val userLocationDecodeJSON: Either[String, UserLocation] =
    userLocationDecoder
      .decodeJson(userLocationString)

  val userAvatar: Avatar = Avatar.withName("AV_1")
  val userAvatarString: String = "\"AV_1\""
  val avatarEncodeJSON: CharSequence = avatarEncoder.encodeJson(userAvatar)
  val avatarDecodeJSON: Either[String, Avatar] = avatarDecoder.decodeJson("\"AV_11\"")
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
        assert(userLocationEncodeJSON)(Assertion.equalTo(userLocationString))
      },
      test("decoding") {
        assert(userLocationDecodeJSON)(Assertion.equalTo(Right(fakeUserLocation)))
      }
    ),
    suite("user avatar")(
      test("encoding") {
        assert(avatarEncodeJSON)(Assertion.equalTo(userAvatarString))
      },
      test("decoding") {
        assert(avatarDecodeJSON)(Assertion.equalTo(Right(userAvatar)))
      }
    )
  )
}
