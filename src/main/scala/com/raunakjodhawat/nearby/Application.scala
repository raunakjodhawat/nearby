package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.controllers.Controller
import com.raunakjodhawat.nearby.models.user.UsersTable
import zio.http._
import zio._
import slick.jdbc.PostgresProfile.api._
import zio.http.endpoint.EndpointMiddleware.None.Err

object Application extends ZIOAppDefault {
  private def initializeDB: ZIO[Any, Throwable, Database] = (for {
    db <- ZIO.attempt(Database.forConfig("postgres"))
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

  val db = Database.forConfig("postgres")

  private val base_path: Path = Root / "api" / "v1"

  private val app: HttpApp[Database, Response] =
    Controller(base_path, db)
  override def run = initializeDB *> Server.serve(app).provide(Server.default)
}
