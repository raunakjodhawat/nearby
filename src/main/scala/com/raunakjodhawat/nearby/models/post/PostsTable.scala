package com.raunakjodhawat.nearby.models.post

import com.raunakjodhawat.nearby.models.user.UsersTable

import java.util.Date
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

val users = TableQuery[UsersTable]

object PostsTable {
  implicit val dateMapping: TypedType[Date] = MappedColumnType.base[Date, String](
    e => e.toString,
    _ => new Date()
  )
}
class PostsTable(tag: Tag) extends Table[Post](tag, "POSTS") {
  import PostsTable._

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def user_id = column[Long]("USER_ID")
  def user_id_fk = foreignKey("USER_ID_FK", user_id, users)(_.id,
                                                            onUpdate = ForeignKeyAction.Restrict,
                                                            onDelete = ForeignKeyAction.Cascade
  )
  def title = column[String]("TITLE")
  def content = column[Option[String]]("CONTENT")
  def createdAt = column[Option[Date]]("CREATED_AT")
  def updatedAt = column[Option[Date]]("UPDATED_AT")

  def * : ProvenShape[Post] = (id, user_id, title, content, createdAt, updatedAt).mapTo[Post]
}
