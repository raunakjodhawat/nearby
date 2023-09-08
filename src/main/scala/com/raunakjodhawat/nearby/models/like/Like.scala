package com.raunakjodhawat.nearby.models.like

import java.util.Date

case class Like(
  id: Option[Long],
  post_id: Long,
  user_id: Long,
  created_at: Option[Date] = Some(new Date())
)
