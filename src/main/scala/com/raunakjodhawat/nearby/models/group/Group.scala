package com.raunakjodhawat.nearby.models.group

import java.util.Date

case class Group(
  id: Long,
  name: String,
  description: Option[String],
  creator_id: Long,
  admins: Option[Seq[Long]] = Some(Seq.empty[Long]),
  members: Option[Seq[Long]] = Some(Seq.empty[Long]),
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date())
)
