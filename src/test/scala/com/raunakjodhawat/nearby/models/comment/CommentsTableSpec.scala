package com.raunakjodhawat.nearby.models.comment

import com.raunakjodhawat.nearby.models.post.{Post, PostsTable}
import com.raunakjodhawat.nearby.models.user.{User, UsersTable}
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.dbio.DBIO
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.junit.JUnitRunnableSpec

import java.util.Date
import scala.util.Properties

object CommentsTableSpec {
  val dbZIO = ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val comments = TableQuery[CommentsTable]
  val users = TableQuery[UsersTable]
  val posts = TableQuery[PostsTable]
  val dateLong: Long = 1690788936
  val date: Date = new Date(dateLong)
}
class CommentsTableSpec extends JUnitRunnableSpec {
  import CommentsTableSpec._

  def clearAndCreateCommentsTableZIO: ZIO[Database, Throwable, Seq[CommentsTable#TableElementType]] = {
    val user: User = User(id = 1L, username = "raunak", password = "sha-256", email = "raunakjodhawat@gmail.com")
    val post: Post = Post(id = 1L, user_id = 1L, title = "My First Post", content = Some("Some image url"))
    for {
      db <- dbZIO
      dbCreationFuture <- ZIO.fromFuture { ex =>
        {
          db.run(
            DBIO.seq(
              comments.schema.dropIfExists,
              posts.schema.dropIfExists,
              users.schema.dropIfExists,
              users.schema.create,
              posts.schema.create,
              comments.schema.create,
              users += user,
              posts += post
            )
          )
        }
      }.fork
      _ <- dbCreationFuture.join
      allComments <- ZIO.fromFuture { ex => db.run(comments.result) }
      _ <- ZIO.from(db.close())
    } yield allComments
  }

  def spec = suite("Comments Table")(
    test("should be able to create comment table") {
      assertZIO(clearAndCreateCommentsTableZIO)(Assertion.equalTo(Seq.empty))
    },
    test("Post Content conversion") {
      val postContent = PostContent("My first comment", Some("This is an amazing website"))
      val postContent_2 = PostContent("My Second Comment, huge like to your work!", None)
      val comment_1_master: Comment = Comment(id = 1L,
                                              user_id = 1L,
                                              post_id = 1L,
                                              post_content = Some(postContent),
                                              created_at = Some(date),
                                              updated_at = Some(date)
      )
      val comment_2_master: Comment = Comment(id = 2L,
                                              user_id = 1L,
                                              post_id = 1L,
                                              post_content = Some(postContent_2),
                                              created_at = Some(date),
                                              updated_at = Some(date)
      )
      val commentsZIO = for {
        a <- clearAndCreateCommentsTableZIO.fork
        _ <- a.join
        db <- dbZIO
        addingComments <- ZIO.fromFuture { ex =>
          db.run(
            DBIO.seq(
              comments += comment_1_master,
              comments += comment_2_master
            )
          )
        }.fork
        _ <- addingComments.join
        allComments <- ZIO.fromFuture { ex => db.run(comments.result) }
        _ <- ZIO.from(db.close())
      } yield allComments

      assertZIO(commentsZIO)(Assertion.hasSize(equalTo(2)))
    }
  ).provide(ZLayer.fromZIO(dbZIO)) @@ TestAspect.sequential
}
