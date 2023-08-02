package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe._
import io.circe.generic.auto._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, JsonError}
import zio.json.internal.{RetractReader, Write}
import zio.prelude.data.Optional.AllValuesAreNullable

import java.util.Date

object JsonEncoderDecoder {
  implicit val encodeDate: Encoder[Date] = Encoder.encodeString.contramap[Date](_.getTime.toString)
  implicit val decodeDate: Decoder[Date] = Decoder.decodeString.map(s => new Date(s.toLong))

  implicit val encodeUserLocation: JsonEncoder[UserLocation] = DeriveJsonEncoder.gen[UserLocation]
  implicit val decodeUserLocation: JsonDecoder[UserLocation] = DeriveJsonDecoder.gen[UserLocation]

  implicit val loginStatusEncoder: JsonEncoder[UserLoginStatus] =
    (a: UserLoginStatus, indent: Option[Int], out: Write) => out.write(s""""${a.toString}"""")
  implicit val loginStatusDecoder: JsonDecoder[UserLoginStatus] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    UserLoginStatus.withName(json.substring(1, json.length - 1))
  }

  implicit val avatarEncoder: JsonEncoder[Avatar] = (a: Avatar, indent: Option[Int], out: Write) =>
    out.write(s""""${a.toString}"""")

  implicit val avatarDecoder: JsonDecoder[Avatar] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    Avatar.withName(json)
  }
  implicit val userStatusEncoder: JsonEncoder[UserStatus] = (a: UserStatus, indent: Option[Int], out: Write) =>
    out.write(s""""${a.toString}"""")
  implicit val userStatusDecoder: JsonDecoder[UserStatus] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    UserStatus.withName(json.substring(1, json.length - 1))
  }

  implicit val userEncoder: Encoder[User] =
    deriveEncoder[User]

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
}
