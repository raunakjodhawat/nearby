package com.raunakjodhawat.nearby.controllers
import com.raunakjodhawat.nearby.repository.user.UserRepository
import slick.jdbc.PostgresProfile.api._
import zio.http._

import scala.concurrent.ExecutionContext.Implicits.global
object Controller {
  def apply(base_path: Path, db: Database): Http[Any, Throwable, Request, Response] = {
    val userRepository = new UserRepository(db)
    val base_route = Http.collect[Request] { case _ -> base_path / "ping" =>
      Response.text("pong")
    }
    val route = base_route ++
      new UserController(base_path, userRepository).api_route ++
      new VerificationController(base_path, userRepository).verify_api_route
    route
  }
}
