package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.controllers.Controller
import com.raunakjodhawat.nearby.models.user.UsersTable
import zio.http._
import zio._
import slick.jdbc.PostgresProfile.api._

object Application extends ZIOAppDefault {
  val dbZIO = ZIO.attempt(Database.forConfig("postgres"))
  private def initializeDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            TableQuery[UsersTable].schema.dropIfExists,
            TableQuery[UsersTable].schema.createIfNotExists
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

  private val base_path: Path = Root / "api" / "v1"
  private val app: HttpApp[Database, Response] = Controller(base_path, dbZIO)
  override def run = initializeDB *> Server.serve(app).provide(Server.default)
}
