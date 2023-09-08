package com.raunakjodhawat.nearby.models.friendship
import java.util.Date

/**
 * id serial PRIMARY KEY,
  user_id_1 int NOT NULL REFERENCES users(id),
  user_id_2 int NOT NULL REFERENCES users(id),
  status enum('following', 'requested', 'blocked') NOT NULL,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
 */
case class Friendship(
  id: Option[Long],
  from_user: Long,
  to_user: Long,
  status: String,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date]
)
