package com.raunakjodhawat.nearby.models.comment

import com.raunakjodhawat.nearby.models.post.PostsTable
import com.raunakjodhawat.nearby.models.user.UsersTable
import slick.jdbc.PostgresProfile.api.*
import slick.dbio.DBIO
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.junit.JUnitRunnableSpec

import scala.util.Properties

object CommentsTableSpec {
  val dbZIO = ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val comments = TableQuery[CommentsTable]
  val users = TableQuery[UsersTable]
  val posts = TableQuery[PostsTable]
}
class CommentsTableSpec extends JUnitRunnableSpec {
  import CommentsTableSpec._
  def spec = suite("Comments Table")(
    test("should be able to create comment table") {
      val zio: ZIO[Database, Throwable, Seq[CommentsTable#TableElementType]] = for {
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
                comments.schema.create
              )
            )
          }
        }.fork
        _ <- dbCreationFuture.join
        allComments <- ZIO.fromFuture { ex => db.run(comments.result) }
        _ <- ZIO.from(db.close())
      } yield allComments
      assertZIO(zio)(Assertion.equalTo(Seq.empty))
    }
  ).provide(ZLayer.fromZIO(dbZIO))
}
