package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.controllers.Controller
import com.raunakjodhawat.nearby.models.user.UsersTable

import scala.util.{Failure, Success}
import zio.http._
import zio._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
object Application extends ZIOAppDefault {
  val db = Database.forConfig("postgres")
  db.run(
    DBIO.seq(
      TableQuery[UsersTable].schema.dropIfExists,
      TableQuery[UsersTable].schema.createIfNotExists
    )
  ).onComplete {
    case Success(_)         => println("Database Initialization complete")
    case Failure(exception) => println(exception.getMessage)
  }
  private val base_path: Path = Root / "api" / "v1"

  private val app = Controller(base_path, db).mapError(e => Response.fromHttpError(HttpError.BadRequest(e.getMessage)))
  override def run = Server.serve(app).provide(Server.default)
}
