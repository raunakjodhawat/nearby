package com.raunakjodhawat.nearby.controllers
import com.raunakjodhawat.nearby.repository.user.UserRepository
import slick.jdbc.PostgresProfile.api._
import zio.http._

import scala.concurrent.ExecutionContext.Implicits.global
object Controller {
  def apply(basePath: Path, db: Database): Http[Any, Throwable, Request, Response] = {
    val userRepository = new UserRepository(db)
    new UserController(basePath, userRepository).api_route ++
      new VerificationController(basePath, userRepository).verify_api_route
  }
}
