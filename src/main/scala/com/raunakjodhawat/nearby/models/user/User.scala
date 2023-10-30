package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.utils.Utils
import com.raunakjodhawat.nearby.utils.Utils.{generateSalt, hashPassword}

import java.util.Date
enum Avatar(val name: String) {
  case AV_1 extends Avatar("AV_1")
  case AV_2 extends Avatar("AV_2")
  case AV_3 extends Avatar("AV_3")
  case AV_4 extends Avatar("AV_4")
  case AV_5 extends Avatar("AV_5")
}

case class UserLocation(lat: Double, long: Double)

case class User(
  id: Long = 0,
  username: String,
  password: String,
  secret: String,
  email: Option[String] = None,
  name: Option[String] = None,
  bio: Option[String] = None,
  phone: Option[String] = None,
  location: Option[UserLocation] = None,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date()),
  activationComplete: Boolean = false,
  avatar: Option[Avatar] = Some(Avatar.AV_1)
)

case class LoginUser(
  username: String,
  password: String
) {
  def toUser: User = {
    val salt = generateSalt
    User(
      username = username,
      password = hashPassword(password, salt),
      secret = salt
    )
  }
}

case class LoginResponse(
  token: String
)
