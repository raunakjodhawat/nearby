package com.raunakjodhawat.nearby.models.like

import java.util.Date

/**
 * CREATE TABLE likes (
  id serial PRIMARY KEY,
  post_id int NOT NULL REFERENCES posts(id),
  user_id int NOT NULL REFERENCES users(id),
  created_at timestamp NOT NULL
);
 */
case class Like(
  id: Option[Long],
  post_id: Long,
  user_id: Long,
  created_at: Option[Date] = Some(new Date())
)
