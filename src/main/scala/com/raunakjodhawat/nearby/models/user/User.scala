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
  secret: Option[String] = None,
  email: String,
  name: Option[String] = None,
  bio: Option[String] = None,
  phone: Option[String] = None,
  location: Option[UserLocation] = None,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date()),
  status: Option[UserStatus] = Some(UserStatus("INACTIVE")),
  login_status: Option[UserLoginStatus] = Some(UserLoginStatus("PENDING_ACTIVATION")),
  avatar: Option[Avatar] = Some(Avatar("AV_1"))
)
