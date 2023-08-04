package com.raunakjodhawat.nearby.models.user

import java.util.Date

case class UserStatus(name: String)
case class UserLoginStatus(name: String)
case class Avatar(name: String)

case class UserLocation(lat: Double, long: Double)
case class User(
  id: Option[Long],
  username: String,
  password: String,
  secret: Option[String],
  email: String,
  phone: Option[String],
  address: Option[String],
  city: Option[String],
  state: Option[String],
  country: Option[String],
  pincode: Option[String],
  location: Option[UserLocation],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date],
  status: Option[UserStatus] = Some(UserStatus("INACTIVE")),
  login_status: Option[UserLoginStatus] = Some(UserLoginStatus("PENDING_ACTIVATION")),
  avatar: Option[Avatar] = Some(Avatar("AV_1"))
)
