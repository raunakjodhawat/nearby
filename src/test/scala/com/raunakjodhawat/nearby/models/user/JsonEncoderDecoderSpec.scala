package com.raunakjodhawat.nearby.models.user

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

  val userAvatar: Avatar = Avatar("AV_1")
  val userAvatarString: String = "{\"name\": \"AV_1\"}"
  val avatarEncodeJSON: Json = userAvatar.asJson
  val avatarDecodeJSON: Decoder.Result[Avatar] = avatarEncodeJSON.as[Avatar]

  val userStatus: UserStatus = UserStatus("ACTIVE")
  val userStatusString: String = "{\"name\": \"ACTIVE\"}"
  val userStatusEncodeJSON: Json = userStatus.asJson
  val userStatusDecodeJSON: Decoder.Result[UserStatus] = userStatusEncodeJSON.as[UserStatus]

  val userLoginStatus: UserLoginStatus = UserLoginStatus("LOGGED_IN")
  val userLoginStatusString: String = "{\"name\": \"LOGGED_IN\"}"
  val userLoginStatusEncodeJSON: Json = userLoginStatus.asJson
  val userLoginStatusDecodeJSON: Decoder.Result[UserLoginStatus] = userLoginStatusEncodeJSON.as[UserLoginStatus]

  val user: User = User(
    Some(1L),
    "username",
    "user123",
    Some("secret"),
    "email",
    Some("phone"),
    Some("address"),
    Some("city"),
    Some("state"),
    Some("country"),
    Some("zip"),
    Some(UserLocation(2.3, 4.5)),
    Some(date),
    Some(date),
    Some(UserStatus("ACTIVE")),
    Some(UserLoginStatus("LOGGED_IN")),
    Some(Avatar("AV_1"))
  )
  val userEncodeJSON: Json = user.asJson
  val userString: String =
    """
      |{
      |  "id" : 1,
      |  "username" : "username",
      |  "password" : "user123",
      |  "secret" : "secret",
      |  "email" : "email",
      |  "phone" : "phone",
      |  "address" : "address",
      |  "city" : "city",
      |  "state" : "state",
      |  "country" : "country",
      |  "pincode" : "zip",
      |  "location" : {
      |    "lat" : 2.3,
      |    "long" : 4.5
      |  },
      |  "created_at" : "1690788936",
      |  "updated_at" : "1690788936",
      |  "status" : {
      |    "name" : "ACTIVE"
      |  },
      |  "login_status" : {
      |    "name" : "LOGGED_IN"
      |  },
      |  "avatar" : {
      |    "name" : "AV_1"
      |  }
      |}
      |""".stripMargin
  val userDecodeJson: Decoder.Result[User] = userEncodeJSON.as[User]
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
    ),
    suite("user status")(
      test("encoding") {
        parse(userStatusString).map(x => assert(userStatusEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(userStatusDecodeJSON)(Assertion.equalTo(Right(userStatus)))
      }
    ),
    suite("user login status")(
      test("encoding") {
        parse(userLoginStatusString).map(x => assert(userLoginStatusEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(userLoginStatusDecodeJSON)(Assertion.equalTo(Right(userLoginStatus)))
      }
    ),
    suite("user")(
      test("encoding") {
        parse(userString).map(x => assert(userEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(userDecodeJson)(Assertion.equalTo(Right(user)))
      }
    )
  )
}
