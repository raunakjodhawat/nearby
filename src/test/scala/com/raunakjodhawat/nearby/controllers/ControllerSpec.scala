package com.raunakjodhawat.nearby.controllers

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives.complete
import zio.test.Assertion._
import zio.test._
import zio.test.akkahttp.AkkaZIOSpecDefault

class ControllerSpec extends AkkaZIOSpecDefault {
  def spec =
    suite("MySpec")(
      test("my test") {
        assertTrue(true)
      }
    )
}
