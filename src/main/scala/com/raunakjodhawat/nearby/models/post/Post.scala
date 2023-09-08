package com.raunakjodhawat.nearby.models.post

import java.util.Date

case class Post(
  id: Option[Long],
  user_id: Long,
  title: String,
  content: Option[String],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date]
)
