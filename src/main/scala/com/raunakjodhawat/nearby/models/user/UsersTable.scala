package com.raunakjodhawat.nearby.models.user

import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.util.Date

object UsersTable {
  implicit val userStatusMapping: TypedType[UserStatus] = MappedColumnType.base[UserStatus, String](
    e => e.toString,
    s => UserStatus(s)
  )
  implicit val userLoginStatusMapping: TypedType[UserLoginStatus] = MappedColumnType.base[UserLoginStatus, String](
    e => e.toString,
    s => UserLoginStatus(s)
  )
  implicit val avatarMapping: TypedType[Avatar] = MappedColumnType.base[Avatar, String](
    e => e.toString,
    s => Avatar(s)
  )
  implicit val userLocationMapping: TypedType[UserLocation] = MappedColumnType.base[UserLocation, String](
    userLocation => s"${userLocation.lat},${userLocation.long}",
    str => {
      val parts = str.split(",")
      UserLocation(parts(0).toDouble, parts(1).toDouble)
    }
  )
  implicit val dateMapping: TypedType[Date] = MappedColumnType.base[Date, String](
    e => e.toString,
    _ => new Date()
  )
}
class UsersTable(tag: Tag) extends Table[User](tag, "USERS") {
  import UsersTable._
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def username = column[String]("USERNAME", O.Unique, O.Length(32))
  def password = column[String]("PASSWORD", O.Length(32))
  def secret = column[Option[String]]("SECRET", O.Length(36))
  def email = column[String]("EMAIL", O.Unique, O.Length(64))
  def phone = column[Option[String]]("PHONE", O.Length(16))
  def name = column[Option[String]]("SNAME", O.Length(32))
  def bio = column[Option[String]]("BIO", O.Length(255))

  def location = column[Option[UserLocation]]("LOCATION")
  def created_at = column[Option[Date]]("CREATED_AT")
  def updated_at = column[Option[Date]]("UPDATED_AT")
  def status = column[Option[UserStatus]]("STATUS")
  def login_status = column[Option[UserLoginStatus]]("LOGIN_STATUS")
  def avatar = column[Option[Avatar]]("AVATAR")

  def * =
    (id,
     username,
     password,
     secret,
     email,
     name,
     bio,
     phone,
     location,
     created_at,
     updated_at,
     status,
     login_status,
     avatar
    )
      .mapTo[User]
}
