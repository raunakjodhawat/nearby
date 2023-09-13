package com.raunakjodhawat.nearby.models.user

import org.junit.runner.RunWith
import slick.lifted.TableQuery
import zio.test.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

object UsersTableSpec {
  val usersTable = TableQuery[UsersTable]
}
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
