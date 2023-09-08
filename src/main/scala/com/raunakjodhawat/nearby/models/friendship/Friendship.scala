package com.raunakjodhawat.nearby.models.friendship
import java.util.Date

enum FriendshipStatus(val value: String) {
  case Pending extends FriendshipStatus("pending")
  case Accepted extends FriendshipStatus("accepted")
  case Rejected extends FriendshipStatus("rejected")
}
case class Friendship(
  id: Long,
  from_user: Long,
  to_user: Long,
  status: Option[FriendshipStatus],
  created_at: Option[Date] = Some(new Date()),
  updated_at: Option[Date] = Some(new Date())
)
