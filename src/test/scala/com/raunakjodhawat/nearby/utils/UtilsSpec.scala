package com.raunakjodhawat.nearby.utils

import com.raunakjodhawat.nearby.utils.Utils._
import courier._
import org.junit.runner.RunWith
import zio._
import zio.test._
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

@RunWith(classOf[ZTestJUnitRunner])
class UtilsSpec extends JUnitRunnableSpec {
  def spec =
    suite("utils spsc")(
      test("my test") {
        assertTrue(true)
      }
    )
}
