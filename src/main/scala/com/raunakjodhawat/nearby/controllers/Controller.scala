package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.utils.Utils
import com.raunakjodhawat.nearby.utils.Utils.decodeAuthorizationHeader
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.*
import zio.http.Headers

object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val base_path: Path = Root / "api" / "v1"
    val userRepository = new UserRepository(db)
    val uc = new UserController(userRepository)
    val ac = new AuthorizationController(userRepository)
    Http
      .collectZIO[Request] {
        case Method.GET -> base_path / "user" / long(id) =>
          uc.getUserById(id)
        case req @ Method.POST -> base_path / "user" / long(id) =>
          uc.updateUser(req.body, id)
        case req @ Method.POST -> base_path / "authenticate" =>
          ac.authenticateRequest(decodeAuthorizationHeader(req.headers))
        case req @ Method.POST -> base_path / "login" =>
          uc.loginUser(req.body)
        case Method.GET -> base_path / "ping" =>
          ZIO.succeed(Response.text("pong"))
      }
      .mapError(err =>
        Response(
          status = Status.BadRequest,
          headers = Headers(("Content-Type", "application/json")),
          body = Body.fromString(err.getMessage)
        )
      )
  }
}
