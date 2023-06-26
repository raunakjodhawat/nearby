package com.raunakjodhawat.nearby.controllers
import slick.jdbc.PostgresProfile.api._
import zio.http._

object Controller {
  def apply(basePath: Path, db: Database): Http[Any, Throwable, Request, Response] = {
    new UserController(basePath, db).api_route
  }
}
