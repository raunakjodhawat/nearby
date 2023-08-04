package com.raunakjodhawat.nearby.models.user

import slick.lifted.TableQuery
import zio.test._
import zio.test.junit.JUnitRunnableSpec

object UsersTableSpec {
  val usersTable = TableQuery[UsersTable]
}
class UsersTableSpec extends JUnitRunnableSpec {
  def spec = suite("some constants")(
    test(
      "some cons"
    ) {
      assertTrue(true)
    }
  )
}
