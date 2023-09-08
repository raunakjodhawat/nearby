package com.raunakjodhawat.nearby.models.like

import com.raunakjodhawat.nearby.models.post.PostsTable
import com.raunakjodhawat.nearby.models.user.UsersTable
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}
import java.util.Date
val users = TableQuery[UsersTable]
val posts = TableQuery[PostsTable]

class LikesTable(tag: Tag) extends Table[Like](tag, "LIKES") {
  import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.dateMapping

  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def user_id: Rep[Long] = column[Long]("USER_ID")
  def user_id_fk = foreignKey("USER_ID_FK", user_id, users)(_.id,
                                                            onUpdate = ForeignKeyAction.Restrict,
                                                            onDelete = ForeignKeyAction.Cascade
  )
  def post_id: Rep[Long] = column[Long]("POST_ID")
  def post_id_fk = foreignKey("POST_ID_FK", post_id, posts)(_.id,
                                                            onUpdate = ForeignKeyAction.Restrict,
                                                            onDelete = ForeignKeyAction.Cascade
  )
  def created_at: Rep[Option[Date]] = column[Option[Date]]("CREATED_AT")

  def * : ProvenShape[Like] = (id, user_id, post_id, created_at).mapTo[Like]
}
