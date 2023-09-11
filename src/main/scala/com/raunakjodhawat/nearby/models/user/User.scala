package com.raunakjodhawat.nearby.models.user

import java.util.Date

case class UserStatus(name: String)
case class UserLoginStatus(name: String)
case class Avatar(name: String)

case class UserLocation(lat: Double, long: Double)
case class UserName(firstName: String, lastName: String)
case class User(
  id: Long,
  username: String,
  password: String,
  secret: Option[String],
  email: String,
  name: Option[String],
  bio: Option[String],
  phone: Option[String],
  location: Option[UserLocation],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date()),
  status: Option[UserStatus] = Some(UserStatus("INACTIVE")),
  login_status: Option[UserLoginStatus] = Some(UserLoginStatus("PENDING_ACTIVATION")),
  avatar: Option[Avatar] = Some(Avatar("AV_1"))
)
