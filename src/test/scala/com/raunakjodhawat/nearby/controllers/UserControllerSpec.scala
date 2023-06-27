package com.raunakjodhawat.nearby.controllers

import org.scalatest.flatspec.AnyFlatSpec
import zio.http._

object UserControllerSpec {
  trait Environment {
    val basePath: Path = Root / "test"
  }
}
class UserControllerSpec extends AnyFlatSpec {
  "UserController" should "return 200" in new UserControllerSpec.Environment {
    assert(1 == 1)
  }
}
