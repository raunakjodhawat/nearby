package com.raunakjodhawat.nearby.controllers

import org.scalatest.flatspec.AnyFlatSpec
import zio.http._
import slick.jdbc.PostgresProfile.api._

object UserControllerSpec {
  trait Environment {
    val basePath: Path = Root / "test"
    val db: Database = null
    val userController = new UserController(basePath, db)
    val api_route: Http[Any, Throwable, Request, Response] = userController.api_route
  }
}
class UserControllerSpec extends AnyFlatSpec {}
