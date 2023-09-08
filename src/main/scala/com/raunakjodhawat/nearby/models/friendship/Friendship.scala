package com.raunakjodhawat.nearby.models.friendship
import java.util.Date

case class Friendship(
  id: Option[Long],
  from_user: Long,
  to_user: Long,
  status: String,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date]
)
