package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository

import slick.jdbc.PostgresProfile.api._

import zio._
import zio.http._

object Controller {
  def apply(base_path: Path, db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val userRepository = new UserRepository(db)
    val base_route: Http[Database, Throwable, Request, Response] = Http.collect[Request] {
      case _ -> base_path / "ping" =>
        Response.text("pong")
    }
    val user_route: Http[Database, Throwable, Request, Response] =
      new UserController(base_path, userRepository).api_route
    val verification_route: Http[Database, Throwable, Request, Response] =
      new VerificationController(base_path, userRepository).verify_api_route
    val route = base_route ++ user_route ++ verification_route

    route.mapError(err => Response.fromHttpError(HttpError.BadRequest.apply(err.getMessage)))
  }
}
