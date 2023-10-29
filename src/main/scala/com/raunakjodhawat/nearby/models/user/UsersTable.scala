package com.raunakjodhawat.nearby.models.user

import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.Tag

import java.util.Date

object UsersTable {
  given userStatusTypeType: TypedType[UserStatus] = MappedColumnType.base[UserStatus, String](
    userStatus => userStatus.name,
    str => UserStatus.valueOf(str)
  )

  given avatarTypedType: TypedType[Avatar] = MappedColumnType.base[Avatar, String](
    avatar => avatar.name,
    s => Avatar.valueOf(s)
  )

  given userTypedType: TypedType[UserLocation] = MappedColumnType.base[UserLocation, String](
    userLocation => s"${userLocation.lat},${userLocation.long}",
    str => {
      val parts = str.split(",")
      UserLocation(parts(0).toDouble, parts(1).toDouble)
    }
  )
}
class UsersTable(tag: Tag) extends Table[User](tag, "USERS") {
  import UsersTable.{avatarTypedType, userStatusTypeType, userTypedType}
  import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.dateMapping
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
  def user_status = column[Option[UserStatus]]("STATUS")
  def avatar = column[Option[Avatar]]("AVATAR")

  def * =
    (id, username, password, secret, email, name, bio, phone, location, created_at, updated_at, user_status, avatar)
      .mapTo[User]
}
