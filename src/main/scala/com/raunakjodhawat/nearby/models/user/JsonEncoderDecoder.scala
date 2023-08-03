package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import java.util.Date

object JsonEncoderDecoder {
  implicit val encodeDate: Encoder[Date] = Encoder.encodeString.contramap[Date](_.getTime.toString)
  implicit val decodeDate: Decoder[Date] = Decoder.decodeString.map(s => new Date(s.toLong))

  implicit val encodeUserLocation: Encoder[UserLocation] = deriveEncoder[UserLocation]
  implicit val decodeUserLocation: Decoder[UserLocation] = deriveDecoder[UserLocation]

  implicit val loginStatusEncoder: Encoder[UserLoginStatus] = deriveEncoder[UserLoginStatus]
  implicit val loginStatusDecoder: Decoder[UserLoginStatus] = deriveDecoder[UserLoginStatus]

  implicit val avatarEncoder: Encoder[Avatar] = deriveEncoder[Avatar]
  implicit val avatarDecoder: Decoder[Avatar] = deriveDecoder[Avatar]

  implicit val userStatusEncoder: Encoder[UserStatus] = deriveEncoder[UserStatus]
  implicit val userStatusDecoder: Decoder[UserStatus] = deriveDecoder[UserStatus]

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
}
