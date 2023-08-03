package com.raunakjodhawat.nearby.models.user

import cats.implicits.toFunctorOps
import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._
import io.circe._
import io.circe.parser._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import java.util.Date
import zio.Scope
import zio.test.{Spec, TestEnvironment}
import zio.test._
import zio.test.junit.JUnitRunnableSpec

object JsonEncoderDecoderSpec {
  val dateLong: Long = 1690788936
  val date: Date = new Date(dateLong)
  val dateEncodeJSON: Json = date.asJson
  val dateDecodeJSON: Decoder.Result[Date] = dateEncodeJSON.as[Date]

  val fakeUserLocation: UserLocation = UserLocation(100, 100)
  val userLocationString: String = "{\"lat\" : 100.0,\"long\" : 100.0}"
  val userLocationEncodeJSON: Json = fakeUserLocation.asJson
  val userLocationDecodeJSON: Decoder.Result[UserLocation] =
    userLocationEncodeJSON.as[UserLocation]

  val userAvatar: Avatar = Avatar.AV_1
  val userAvatarString: String = "\"AV_4\""
  val avatarEncodeJSON: Json = userAvatar.asJson
  val avatarDecodeJSON: Decoder.Result[Avatar] = avatarDecoder.as[Avatar]
//
//  val userStatus: UserStatus = UserStatus.withName("ACTIVE")
//  val userStatusString: String = "\"ACTIVE\""
//  val userStatusEncodeJSON: CharSequence = userStatusEncoder.encodeJson(userStatus)
//  val userStatusDecodeJSON: Either[String, UserStatus] = userStatusDecoder.decodeJson(userStatusString)
}
class JsonEncoderDecoderSpec extends JUnitRunnableSpec {
  import JsonEncoderDecoderSpec._
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Json Encoder Decoder")(
    suite("Java Util Date")(
      test("encoding") {
        assert(dateEncodeJSON)(Assertion.equalTo("1690788936".asJson))
      },
      test("decoding") {
        assert(dateDecodeJSON)(Assertion.equalTo(Right(date)))
      }
    ),
    suite("user location")(
      test("encoding") {
        parse(userLocationString).map(x => assert(userLocationEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(userLocationDecodeJSON)(Assertion.equalTo(Right(fakeUserLocation)))
      }
    ),
    suite("user avatar")(
      test("encoding") {
        parse(userAvatarString).map(x => assert(avatarEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(avatarDecodeJSON)(Assertion.equalTo(Right(userAvatar)))
      }
    )
  )
}
