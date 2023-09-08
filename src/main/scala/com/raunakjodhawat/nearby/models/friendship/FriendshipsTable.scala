package com.raunakjodhawat.nearby.models.friendship

import com.raunakjodhawat.nearby.models.user.UsersTable

import java.util.Date
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

object FriendshipsTable {
  given friendshipStatusTypedType: TypedType[FriendshipStatus] = MappedColumnType.base[FriendshipStatus, String](
    e => e.toString,
    str => FriendshipStatus.valueOf(str)
  )
}

val users = TableQuery[UsersTable]
class FriendshipsTable(tag: Tag) extends Table[Friendship](tag, "FRIENDSHIPS") {
  import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.dateMapping
  import FriendshipsTable.friendshipStatusTypedType
  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def from_user_id: Rep[Long] = column[Long]("FROM_USER_ID")

  def from_user_id_fk = foreignKey("FROM_USER_ID_FK", from_user_id, users)(_.id,
                                                                           onUpdate = ForeignKeyAction.Restrict,
                                                                           onDelete = ForeignKeyAction.Cascade
  )
  def to_user_id: Rep[Long] = column[Long]("TO_USER_ID")

  def to_user_id_fk = foreignKey("TO_USER_ID_FK", to_user_id, users)(_.id,
                                                                     onUpdate = ForeignKeyAction.Restrict,
                                                                     onDelete = ForeignKeyAction.Cascade
  )
  def status = column[Option[FriendshipStatus]]("STATUS")
  def created_at: Rep[Option[Date]] = column[Option[Date]]("CREATED_AT")
  def updated_at: Rep[Option[Date]] = column[Option[Date]]("UPDATED_AT")

  def * : ProvenShape[Friendship] =
    (id, from_user_id, to_user_id, status, created_at, updated_at).mapTo[Friendship]

}
