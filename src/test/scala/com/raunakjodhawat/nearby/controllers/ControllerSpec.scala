package com.raunakjodhawat.nearby.controllers

import org.junit.runner.RunWith
import zio.test.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

@RunWith(classOf[ZTestJUnitRunner])
class ControllerSpec extends JUnitRunnableSpec {
  def spec =
    suite("MySpec")(
      test("my test") {
        assertTrue(true)
      }
    )
}
