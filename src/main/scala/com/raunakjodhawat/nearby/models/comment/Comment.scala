package com.raunakjodhawat.nearby.models.comment

import com.raunakjodhawat.nearby.models.post.Post

import java.util.Date

/**
 * CREATE TABLE comments (
  *id serial PRIMARY KEY,
  *post_id int NOT NULL REFERENCES posts(id),
  *user_id int NOT NULL REFERENCES users(id),
  *body text,
  *created_at timestamp NOT NULL,
  *updated_at timestamp NOT NULL
*);
 *
 */
case class Comment(
  id: Option[Long],
  post_id: Long,
  user_id: Long,
  body: Post,
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date]
)
