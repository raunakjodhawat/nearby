package com.raunakjodhawat.nearby.models.user

import org.junit.runner.RunWith
import zio.test._
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

@RunWith(classOf[ZTestJUnitRunner])
class UsersTableSpec extends JUnitRunnableSpec {
  def spec = suite("some constants")(
    test(
      "some cons"
    ) {
      assertTrue(true)
    }
  )
}
