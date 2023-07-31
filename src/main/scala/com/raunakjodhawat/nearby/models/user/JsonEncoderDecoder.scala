package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, JsonError}
import zio.json.internal.{RetractReader, Write}

import java.util.Date

object JsonEncoderDecoder {
  implicit val dateDecoder: JsonDecoder[Date] = JsonDecoder[String].map(s => new Date(s.toLong))
  implicit val dateEncoder: JsonEncoder[Date] = JsonEncoder[Long].contramap(_.getTime)

  implicit val userLocationEncoder: JsonEncoder[UserLocation] = DeriveJsonEncoder.gen[UserLocation]
  implicit val userLocationDecoder: JsonDecoder[UserLocation] = DeriveJsonDecoder.gen[UserLocation]

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
    println(json.substring(1, json.length - 1))
    Avatar.withName(json.substring(1, json.length - 1))
  }
  implicit val userStatusEncoder: JsonEncoder[UserStatus] = (a: UserStatus, indent: Option[Int], out: Write) =>
    out.write(s""""${a.toString}"""")
  implicit val userStatusDecoder: JsonDecoder[UserStatus] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    UserStatus.withName(json.substring(1, json.length - 1))
  }
  implicit val userDecoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  implicit val userEncoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
}
