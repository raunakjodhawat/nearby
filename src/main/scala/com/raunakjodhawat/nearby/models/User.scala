package com.raunakjodhawat.nearby.models

import java.util.Date

enum UserStatus {
  case ACTIVE
  case INACTIVE
  case Deleted
}

enum UserLoginStatus {
  case LoggedIn
  case LoggedOut
}

case class UserLocation(lat: Double, long: Double)

case class User(
  username: String,
  password: String,
  salt: String,
  email: String,
  phone: String,
  address: Option[String],
  city: Option[String],
  state: Option[String],
  country: Option[String],
  pincode: Option[String],
  status: UserStatus = UserStatus.ACTIVE,
  loginStatus: UserLoginStatus = UserLoginStatus.LoggedOut,
  created_at: Date,
  updated_at: Date,
  location: Option[UserLocation]
)
