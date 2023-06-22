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
      TableQuery[UsersTable].schema.createIfNotExists
    )
  ).onComplete {
    case Success(value)     => println("Success")
    case Failure(exception) => println(exception.getMessage)
  }
  private val base_path: Path = Root / "api" / "v1"
  private val base_route = Http.collect[Request] { case _ -> base_path / "ping" =>
    Response.text("pong")
  }
  private val app =
    base_route ++ Controller(base_path, db).mapError(e => Response.fromHttpError(HttpError.BadRequest(e.getMessage)))
  override def run = Server.serve(app).provide(Server.default)
}
