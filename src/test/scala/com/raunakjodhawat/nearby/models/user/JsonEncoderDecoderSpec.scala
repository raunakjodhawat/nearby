package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder.*
import com.raunakjodhawat.nearby.testUser
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import org.junit.runner.RunWith

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

  val test_user: User = testUser()
  val test_user_json: Json = test_user.asJson
  val test_user_json_string: String =
    """
      |{
      |  "id" : 1,
      |  "username" : "username",
      |  "password" : "password",
      |  "secret" : "secret",
      |  "email" : "email",
      |  "name" : "name",
      |  "bio" : "bio",
      |  "phone" : "phone",
      |  "location" : {
      |    "lat" : 2.3,
      |    "long" : 4.5
      |  },
      |  "created_at" : "1690788936",
      |  "updated_at" : "1690788936",
      |  "activationComplete" : false,
      |  "avatar" : "AV_1"
      |}
      |""".stripMargin
  val test_user_decoder_json: Decoder.Result[User] = test_user_json.as[User]

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
    suite("user")(
      test("encoding") {
        parse(test_user_json_string).map(x => assert(test_user_json)(Assertion.equalTo(x)))
      },
      test("decoding") {
        assert(test_user_decoder_json)(Assertion.equalTo(Right(test_user)))
      }
    )
  )
}
