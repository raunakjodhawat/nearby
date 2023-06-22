package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.UsersTable
import zio._
import zio.test._
import zio.test.Assertion._

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._
object UserRepositorySpec extends ZIOSpecDefault {
  val db = Database.forConfig("postgres")
  val userRepository = new UserRepository(db)
  def spec = suite("UserRepositorySpec")(
    test("getAllUsers") {
      val allUsers = userRepository.getAllUsers().join
      assert(allUsers)(equalTo(Seq[UsersTable#TableElementType]()))
    }
  )
}
