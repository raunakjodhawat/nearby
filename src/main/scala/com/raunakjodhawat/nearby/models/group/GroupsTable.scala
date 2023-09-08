package com.raunakjodhawat.nearby.models.group

import com.raunakjodhawat.nearby.models.user.UsersTable
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

import java.util.Date

object GroupsTable {
  given seqLongTypedType: TypedType[Seq[Long]] = MappedColumnType.base[Seq[Long], String](
    e => e.mkString("[", ",", "]"),
    s => s.substring(1, s.length - 1).split(",").map(_.toLong).toSeq
  )
}
val users = TableQuery[UsersTable]
class GroupsTable(tag: Tag) extends Table[Group](tag, "GROUPS") {
  import GroupsTable.seqLongTypedType
  import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.dateMapping
  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("NAME")
  def description: Rep[Option[String]] = column[Option[String]]("DESCRIPTION")
  def creator_id: Rep[Long] = column[Long]("CREATOR")

  def creator_id_fk = foreignKey("CREATOR_ID_FK", creator_id, users)(_.id,
                                                                     onUpdate = ForeignKeyAction.Restrict,
                                                                     onDelete = ForeignKeyAction.Cascade
  )
  def admins: Rep[Option[Seq[Long]]] = column[Option[Seq[Long]]]("ADMINS")
  def members: Rep[Option[Seq[Long]]] = column[Option[Seq[Long]]]("MEMBERS")

  def created_at: Rep[Option[Date]] = column[Option[Date]]("CREATED_AT")
  def updated_at: Rep[Option[Date]] = column[Option[Date]]("UPDATED_AT")

  override def * : ProvenShape[Group] =
    (id, name, description, creator_id, admins, members, created_at, updated_at).mapTo[Group]
}
