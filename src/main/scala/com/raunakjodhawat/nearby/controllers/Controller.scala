package com.raunakjodhawat.nearby.controllers
import slick.jdbc.PostgresProfile
import zio.http._

object Controller {
  def apply(basePath: Path, db: PostgresProfile.backend.Database): Http[Any, Throwable, Request, Response] = {
    new UserController(basePath, db).api_route
  }
}
