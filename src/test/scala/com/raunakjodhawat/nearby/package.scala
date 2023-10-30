package com.raunakjodhawat

import com.raunakjodhawat.nearby.models.comment.CommentsTable
import com.raunakjodhawat.nearby.models.common.typedtypes.Mappings.{dateToLong, dateToString}
import com.raunakjodhawat.nearby.models.post.PostsTable
import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation, UsersTable}
import io.circe.Encoder.*
import io.circe.{Json, JsonNumber, JsonObject}
import io.circe.Encoder.encodeJsonObject
import slick.jdbc
import slick.jdbc.PostgresProfile
import zio.{Task, ZIO}
import slick.jdbc.PostgresProfile.api.*
import zio.test.*
import zio.test.Assertion.*

import scala.util.Properties
import java.util.Date

package object nearby {
  val test_comments = TableQuery[CommentsTable]
  val test_users = TableQuery[UsersTable]
  val test_posts = TableQuery[PostsTable]
  val test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))

  val test_dateLong: Long = 1690788936
  val test_date: Date = new Date(test_dateLong)
  def clearDB(): ZIO[Any, Throwable, jdbc.PostgresProfile.backend.JdbcDatabaseDef] = {
    for {
      db <- test_dbZIO
      dbCreationFuture <- ZIO.fromFuture { ex =>
        {
          db.run(
            DBIO.seq(
              test_comments.schema.dropIfExists,
              test_posts.schema.dropIfExists,
              test_users.schema.dropIfExists,
              test_users.schema.create,
              test_posts.schema.create,
              test_comments.schema.create
            )
          )
        }
      }.fork
      _ <- dbCreationFuture.join
      _ <- ZIO.from(db.close())
    } yield db
  }
  def testUser(created_at: Date = test_date, updated_at: Date = test_date): User = User(
    1L,
    "username",
    "password",
    "secret",
    Some("email"),
    Some("name"),
    Some("bio"),
    Some("phone"),
    Some(UserLocation(2.3, 4.5)),
    Some(created_at),
    Some(updated_at),
    false,
    Some(Avatar.AV_1)
  )

  def customAssertZIO[A, B, C](zio: ZIO[A, B, C]): ZIO[A, Nothing, TestResult] = {
    val assertionZIO = zio.fold(
      _ => ZIO.unit,
      _ => ZIO.fail("Assertion failed")
    )

    assertZIO(assertionZIO)(Assertion.equalTo(ZIO.unit))
  }
}
