package com.raunakjodhawat.nearby.models.comment

import java.util.Date

import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{NestedShapeLevel, ProvenShape, Tag}
import scala.reflect.ClassTag

object CommentsTable {
  implicit val postContentTypedType: TypedType[PostContent] = MappedColumnType.base[PostContent, String](
    e => e.toString,
    str => PostContent("str", "hello")
  )
  implicit val dateMapping: TypedType[Date] = MappedColumnType.base[Date, String](
    e => e.toString,
    _ => new Date()
  )
}

class CommentsTable(tag: Tag) extends Table[Comment](tag, "COMMENTS") {
  import CommentsTable._
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def post_id = column[Long]("POST_ID")
  def user_id = column[Long]("USER_ID")
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
