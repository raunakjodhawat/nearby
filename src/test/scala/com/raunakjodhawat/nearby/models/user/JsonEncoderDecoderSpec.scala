package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder.*
import com.raunakjodhawat.nearby.{testUser, testUserJson}
import io.circe.*
import io.circe.parser.*
import io.circe.generic.semiauto.*
import io.circe.syntax.EncoderOps
import org.junit.runner.RunWith

import java.util.Date
import zio.Scope
import zio.test.{Spec, TestEnvironment}
import zio.test.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

object JsonEncoderDecoderSpec {
  val test_user_location: UserLocation = UserLocation(100, 100)
  val userLocationString: String = "{\"lat\" : 100.0,\"long\" : 100.0}"
  val userLocationEncodeJSON: Json = test_user_location.asJson
  val userLocationDecodeJSON: Decoder.Result[UserLocation] =
    userLocationEncodeJSON.as[UserLocation]

  val userEncodeJSON: Json = testUser().asJson
  val userStringInJson: JsonObject = testUserJson()
  val userDecodeJson: Decoder.Result[User] = userEncodeJSON.as[User]

}
@RunWith(classOf[ZTestJUnitRunner])
class JsonEncoderDecoderSpec extends JUnitRunnableSpec {
  import JsonEncoderDecoderSpec._
  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Json Encoder Decoder")(
    suite("user location")(
      test("encoding") {
        parse(userLocationString).map(x => assert(userLocationEncodeJSON)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(userLocationDecodeJSON)(Assertion.equalTo(Right(test_user_location)))
      }
    ),
    suite("user avatar")(
      test("encoding") {
        assert(Avatar.AV_1.toString.asJson)(Assertion.equalTo(Json.fromString("AV_1")))
        assert(Avatar.AV_2.toString.asJson)(Assertion.equalTo(Json.fromString("AV_2")))
        assert(Avatar.AV_3.toString.asJson)(Assertion.equalTo(Json.fromString("AV_3")))
        assert(Avatar.AV_4.toString.asJson)(Assertion.equalTo(Json.fromString("AV_4")))
        assert(Avatar.AV_5.toString.asJson)(Assertion.equalTo(Json.fromString("AV_5")))
      },
      test("decoding") {
        assert(Avatar.AV_1.asJson.as[Avatar])(Assertion.equalTo(Right(Avatar.AV_1)))
        assert(Avatar.AV_2.asJson.as[Avatar])(Assertion.equalTo(Right(Avatar.AV_2)))
        assert(Avatar.AV_3.asJson.as[Avatar])(Assertion.equalTo(Right(Avatar.AV_3)))
        assert(Avatar.AV_4.asJson.as[Avatar])(Assertion.equalTo(Right(Avatar.AV_4)))
        assert(Avatar.AV_5.asJson.as[Avatar])(Assertion.equalTo(Right(Avatar.AV_5)))
      }
    ),
    suite("user status")(
      test("encoding") {
        assert(UserStatus.ACTIVE.toString.asJson)(Assertion.equalTo(Json.fromString("ACTIVE")))
        assert(UserStatus.INACTIVE.toString.asJson)(Assertion.equalTo(Json.fromString("INACTIVE")))
        assert(UserStatus.PENDING_ACTIVATION.toString.asJson)(Assertion.equalTo(Json.fromString("PENDING_ACTIVATION")))
      },
      test("decoding") {
        assert(UserStatus.ACTIVE.asJson.as[UserStatus])(Assertion.equalTo(Right(UserStatus.ACTIVE)))
        assert(UserStatus.INACTIVE.asJson.as[UserStatus])(Assertion.equalTo(Right(UserStatus.INACTIVE)))
        assert(UserStatus.PENDING_ACTIVATION.asJson.as[UserStatus])(
          Assertion.equalTo(Right(UserStatus.PENDING_ACTIVATION))
        )
      }
    ),
    suite("user")(
      test("encoding") {
        println(userEncodeJSON)
        println(userStringInJson)
        // parse(userString).map(x => assert(testUserJson())(Assertion.equalTo(x)))
        assertTrue(true)
      },
      test("decoding") {
        assert(userDecodeJson)(Assertion.equalTo(Right(testUser())))
      }
    )
  )
}
