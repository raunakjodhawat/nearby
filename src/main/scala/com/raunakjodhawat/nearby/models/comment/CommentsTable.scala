package com.raunakjodhawat.nearby.models.comment

import com.raunakjodhawat.nearby.models.post.PostsTable
import com.raunakjodhawat.nearby.models.user.UsersTable

import java.util.Date
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

object CommentsTable {
  given postContentTypedType: TypedType[PostContent] = MappedColumnType.base[PostContent, String](
    e => e.toString,
    str => {
      val parts = str.split(",")
      PostContent(parts(0), if (parts.length > 1) Some(parts(1)) else None)
    }
  )
}

val users = TableQuery[UsersTable]
val posts = TableQuery[PostsTable]
class CommentsTable(tag: Tag) extends Table[Comment](tag, "COMMENTS") {
  import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.dateMapping
  import CommentsTable.postContentTypedType
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def post_id = column[Long]("POSTID")
  def post_id_fk = foreignKey("POST_ID_FK", post_id, posts)(_.id,
                                                            onUpdate = ForeignKeyAction.Restrict,
                                                            onDelete = ForeignKeyAction.Cascade
  )

  def user_id = column[Long]("USER_ID")

  def user_id_fk = foreignKey("USER_ID_FK", user_id, users)(_.id,
                                                            onUpdate = ForeignKeyAction.Restrict,
                                                            onDelete = ForeignKeyAction.Cascade
  )
  def post_content = column[Option[PostContent]]("POST")
  def created_at = column[Option[Date]]("CREATED_AT")
  def updated_at = column[Option[Date]]("UPDATED_AT")

  override def * : ProvenShape[Comment] = (
    id,
    post_id,
    user_id,
    post_content,
    created_at,
    updated_at
  ).mapTo[Comment]
}
