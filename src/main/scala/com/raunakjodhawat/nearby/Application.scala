package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.controllers.Controller
import com.raunakjodhawat.nearby.models.comment.CommentsTable
import com.raunakjodhawat.nearby.models.friendship.FriendshipsTable
import com.raunakjodhawat.nearby.models.group.GroupsTable
import com.raunakjodhawat.nearby.models.like.LikesTable
import com.raunakjodhawat.nearby.models.post.PostsTable
import com.raunakjodhawat.nearby.models.user.UsersTable
import zio.http.*
import zio.*
import slick.jdbc.PostgresProfile.api.*

object databaseConfiguration {
  val dbZIO = ZIO.attempt(Database.forConfig("postgres"))
  val comments = TableQuery[CommentsTable]
  val friendships = TableQuery[FriendshipsTable]
  val groups = TableQuery[GroupsTable]
  val likes = TableQuery[LikesTable]
  val posts = TableQuery[PostsTable]
  val users = TableQuery[UsersTable]
  def initializeDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            users.schema.dropIfExists,
            users.schema.create,
            posts.schema.dropIfExists,
            posts.schema.create,
            comments.schema.dropIfExists,
            comments.schema.create,
            likes.schema.dropIfExists,
            likes.schema.create,
            friendships.schema.dropIfExists,
            friendships.schema.create,
            groups.schema.dropIfExists,
            groups.schema.create
          )
        )
      }
    }.fork
    dbUpdateResult <- updateFork.await
  } yield dbUpdateResult match {
    case Exit.Success(_) => ZIO.succeed(println("Database Initialization complete")) *> ZIO.from(db)
    case Exit.Failure(cause) =>
      ZIO.succeed(println(s"Database Initialization errored, ${cause.failures}")) *> ZIO.fail(
        new Exception("Failed to initialize")
      )
  }).flatMap(x => x)
}
object Application extends ZIOAppDefault {
  import databaseConfiguration._

  private val app: HttpApp[Database, Response] = Controller(dbZIO)
  override def run = initializeDB *> Server.serve(app).provide(Server.default, ZLayer.fromZIO(dbZIO))
}
