package com.raunakjodhawat.nearby.models.user

import java.util.Date

enum UserStatus(val name: String) {
  case PENDING_ACTIVATION extends UserStatus("PENDING_ACTIVATION")
  case ACTIVE extends UserStatus("ACTIVE")
  case INACTIVE extends UserStatus("INACTIVE")
}
enum Avatar(val name: String) {
  case AV_1 extends Avatar("AV_1")
  case AV_2 extends Avatar("AV_2")
  case AV_3 extends Avatar("AV_3")
  case AV_4 extends Avatar("AV_4")
  case AV_5 extends Avatar("AV_5")
}

case class UserLocation(lat: Double, long: Double)

case class User(
  id: Long,
  username: String,
  password: String,
  secret: Option[String] = None,
  email: String,
  name: Option[String] = None,
  bio: Option[String] = None,
  phone: Option[String] = None,
  location: Option[UserLocation] = None,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date()),
  user_status: Option[UserStatus] = Some(UserStatus.PENDING_ACTIVATION),
  avatar: Option[Avatar] = Some(Avatar.AV_1)
)
