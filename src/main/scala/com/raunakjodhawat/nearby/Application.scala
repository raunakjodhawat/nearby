package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.controllers.Controller
import com.raunakjodhawat.nearby.models.user.UsersTable
import zio.http._
import zio._
import slick.jdbc.PostgresProfile.api._

object databaseConfiguration {
  val dbZIO = ZIO.attempt(Database.forConfig("postgres"))
  val users = TableQuery[UsersTable]
  def initializeDB: ZIO[Any, Throwable, Database] = (for {
    db <- dbZIO
    updateFork <- ZIO.fromFuture { ex =>
      {
        db.run(
          DBIO.seq(
            users.schema.dropIfExists,
            users.schema.createIfNotExists
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
