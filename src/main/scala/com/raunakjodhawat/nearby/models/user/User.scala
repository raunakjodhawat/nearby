package com.raunakjodhawat.nearby.models.user

import java.util.Date

sealed trait UserStatus
object UserStatus {
  case object ACTIVE extends UserStatus
  case object INACTIVE extends UserStatus
  case object DELETED extends UserStatus

  private val nameMap: Map[String, UserStatus] = Map(
    "ACTIVE" -> ACTIVE,
    "INACTIVE" -> INACTIVE,
    "DELETED" -> DELETED
  )
  def withName(name: String): UserStatus = nameMap.getOrElse(name, INACTIVE)
}

sealed trait UserLoginStatus
object UserLoginStatus {
  case object LOGGED_IN extends UserLoginStatus
  case object LOGGED_OUT extends UserLoginStatus
  case object PENDING_ACTIVATION extends UserLoginStatus

  private val nameMap: Map[String, UserLoginStatus] = Map(
    "LOGGED_IN" -> LOGGED_IN,
    "LOGGED_OUT" -> LOGGED_OUT,
    "PENDING_ACTIVATION" -> PENDING_ACTIVATION
  )
  def withName(name: String): UserLoginStatus = nameMap.getOrElse(name, LOGGED_OUT)
}
sealed trait Avatar
object Avatar {
  case object AV_1 extends Avatar
  case object AV_2 extends Avatar
  case object AV_3 extends Avatar
  case object AV_4 extends Avatar
  case object AV_5 extends Avatar
  case object AV_6 extends Avatar
  case object AV_7 extends Avatar
  case object AV_8 extends Avatar
  case object AV_9 extends Avatar
  case object AV_10 extends Avatar

  val nameMap: Map[String, Avatar] = Map(
    "AV_1" -> AV_1,
    "AV_2" -> AV_2,
    "AV_3" -> AV_3,
    "AV_4" -> AV_4,
    "AV_5" -> AV_5,
    "AV_6" -> AV_6,
    "AV_7" -> AV_7,
    "AV_8" -> AV_8,
    "AV_9" -> AV_9,
    "AV_10" -> AV_10
  )
  def withName(name: String): Avatar = nameMap.getOrElse(name, AV_1)
}
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
  status: Option[UserStatus] = Some(UserStatus.INACTIVE),
  login_status: Option[UserLoginStatus] = Some(UserLoginStatus.PENDING_ACTIVATION),
  avatar: Option[Avatar] = Some(Avatar.withName("AV_1"))
)
