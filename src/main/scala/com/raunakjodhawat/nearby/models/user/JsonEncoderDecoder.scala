package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import com.raunakjodhawat.nearby.models.user.UserLoginStatus.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus.UserStatus
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, JsonError}
import zio.json.internal.{RetractReader, Write}

import java.text.SimpleDateFormat
import java.util.Date

object JsonEncoderDecoder {
  private val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dateDecoder: JsonDecoder[Date] = (trace: List[JsonError], in: RetractReader) => {
    val date: String = in.toString
    df.parse(date)
  }
  implicit val dateEncoder: JsonEncoder[Date] = (a: Date, indent: Option[Int], out: Write) => out.write(df.format(a))

  implicit val userLocationEncoder: JsonEncoder[UserLocation] = (a: UserLocation, indent: Option[Int], out: Write) =>
    out.write(s"""{"latitude":${a.lat},"longitude":${a.long}}""")
  implicit val userLocationDecoder: JsonDecoder[UserLocation] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    val latitude = json.substring(json.indexOf(":") + 1, json.indexOf(",")).toDouble
    val longitude = json.substring(json.lastIndexOf(":") + 1, json.indexOf("}")).toDouble
    UserLocation(latitude, longitude)
  }

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
