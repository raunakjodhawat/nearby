package com.raunakjodhawat.nearby.models.comment

import org.junit.runner.RunWith
import zio.test.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import java.util.Date

object CommentSpec {
  val title = "Post title"
  val content = "Content"
  val postContent = PostContent(title, Some(content))
  val dateLong: Long = 1690788936
  val date: Date = new Date(dateLong)
  val comment = Comment(
    1L,
    1L,
    1L,
    Some(postContent),
    Some(date),
    Some(date)
  )
}
@RunWith(classOf[ZTestJUnitRunner])
class CommentSpec extends JUnitRunnableSpec {
  import CommentSpec._
  def spec = suite("Comments")(
    test("PostContent") {
      assert(postContent.toString)(
        Assertion.equalTo(
          s"PostContent($title,Some($content))"
        )
      )
    },
    test("Comment") {
      assert(comment.toString)(
        Assertion.equalTo(
          s"Comment(1,1,1,Some(PostContent($title,Some($content))),Some($date),Some($date))"
        )
      )
    }
  )
}
