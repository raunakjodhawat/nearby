package com.raunakjodhawat.nearby.models.user

import java.util.Date

object UserStatus extends Enumeration {
  type UserStatus = Value
  val ACTIVE, INACTIVE, DELETED = Value
}
object UserLoginStatus extends Enumeration {
  type UserLoginStatus = Value
  val LOGGED_IN, LOGGED_OUT = Value
}

object Avatar extends Enumeration {
  type Avatar = Value
  val AV_1, AV_2, AV_3, AV_4, AV_5, AV_6, AV_7, AV_8, AV_9, AV_10 = Value
}

import Avatar._
import UserLoginStatus._
import UserStatus._

case class UserLocation(lat: Double, long: Double)
case class User(
  id: Option[Long],
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
  location: Option[UserLocation],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date],
  status: Option[UserStatus] = Some(UserStatus.ACTIVE),
  login_status: Option[UserLoginStatus] = Some(UserLoginStatus.LOGGED_OUT),
  avatar: Option[Avatar] = Some(Avatar.AV_1)
)

class UserAlreadyExistsException(id: Long) extends Exception(s"User with id $id already exists")
class UserDoesNotExistException(id: Option[Long]) extends Exception(s"User with id $id does not exist")
