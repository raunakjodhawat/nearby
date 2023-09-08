package com.raunakjodhawat.nearby.models.comment

import java.util.Date

case class PostContent(
  title: String,
  content: Option[String]
)

case class Comment(
  id: Long,
  post_id: Long,
  user_id: Long,
  post_content: Option[PostContent],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date())
)
