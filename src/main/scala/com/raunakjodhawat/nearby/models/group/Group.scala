package com.raunakjodhawat.nearby.models.group

import java.util.Date

case class Group(
  id: Option[Long],
  name: String,
  description: Option[String],
  creator: Long,
  admins: Seq[Long] = Seq.empty[Long],
  members: Seq[Long] = Seq.empty[Long],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date]
)
