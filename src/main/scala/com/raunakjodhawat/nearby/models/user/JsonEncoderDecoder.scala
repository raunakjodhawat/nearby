package com.raunakjodhawat.nearby.models.user

import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.Date

object JsonEncoderDecoder {
  implicit val encodeDate: Encoder[Date] = Encoder.encodeString.contramap[Date](_.getTime.toString)
  implicit val decodeDate: Decoder[Date] = Decoder.decodeString.map(s => new Date(s.toLong))

  implicit val avatarEncoder: Encoder[Avatar] = Encoder.instance(avatar => Json.fromString(avatar.name))
  implicit val avatarDecoder: Decoder[Avatar] = Decoder.decodeString.map(avatar => Avatar.valueOf(avatar))
  implicit val encodeUserLocation: Encoder[UserLocation] = deriveEncoder[UserLocation]
  implicit val decodeUserLocation: Decoder[UserLocation] = deriveDecoder[UserLocation]

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]

  implicit val loginUserEncoder: Encoder[LoginUser] = deriveEncoder[LoginUser]
  implicit val loginUserDecoder: Decoder[LoginUser] = deriveDecoder[LoginUser]
}
