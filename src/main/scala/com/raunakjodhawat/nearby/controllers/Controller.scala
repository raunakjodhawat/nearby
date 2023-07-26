package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository

import slick.jdbc.PostgresProfile.api._

import zio._
import zio.http._

object Controller {
  def apply(base_path: Path, db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val userRepository = new UserRepository(db)
    val vc = new VerificationController(base_path, userRepository)
    val uc = new UserController(base_path, userRepository)
    Http
      .collectZIO[Request] {
        case Method.GET -> base_path / "verify" / long(id) / secret_key =>
          vc.verifyUser(id, secret_key)
        case Method.GET -> base_path / "user" / long(id) =>
          uc.getUserById(id)
        case req @ Method.POST -> base_path / "user" / long(id) =>
          uc.updateUser(req.body, id)
        case Method.GET -> base_path / "user" =>
          uc.getAllUsers
        case req @ Method.POST -> base_path / "user" =>
          uc.createUser(req.body)
        case Method.GET -> base_path / "ping" =>
          ZIO.succeed(Response.text("pong"))
      }
      .mapError(err => Response.fromHttpError(HttpError.BadRequest.apply(err.getMessage)))
  }
}
