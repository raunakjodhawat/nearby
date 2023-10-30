package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.utils.Utils
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.*
import zio.http.Headers

object Controller {
  def apply(db: ZIO[Any, Throwable, Database]): HttpApp[Database, Response] = {
    val base_path: Path = Root / "api" / "v1"
    val userRepository = new UserRepository(db)
    val vc = new VerificationController(userRepository)
    val uc = new UserController(userRepository)
    val ac = new AuthorizationController(userRepository)
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
        case req @ Method.POST -> base_path / "login" =>
          ac.loginUser(Utils.decodeAuthorizationHeader(req.headers))
        case Method.GET -> base_path / "ping" =>
          ZIO.succeed(Response.text("pong"))
      }
      .mapError(err =>
        Response(
          status = Status.BadRequest,
          headers = Headers(("Content-Type", "application/json")),
          body = Body.fromString(s"""${err.getMessage}""")
        )
      )
  }
}
