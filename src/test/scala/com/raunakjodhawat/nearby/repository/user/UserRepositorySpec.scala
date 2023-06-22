package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.repository.user.UserRepositorySpec.userRepository
import org.junit.runner.RunWith
import com.typesafe.slick.testkit.util.{ExternalJdbcTestDB, ProfileTest, TestDB, Testkit}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.ExecutionContext
import slick.jdbc.{PostgresProfile, ResultSetAction}
import slick.dbio.DBIO
import slick.jdbc.GetResult._
import zio.test.ZIOSpecDefault
import scala.concurrent.ExecutionContext.Implicits.global
object UserRepositorySpec {
  // #tdb
  def tdb = new ExternalJdbcTestDB("mypostgres") {
    // #tdb
    val profile = PostgresProfile

    override def localTables(implicit ec: ExecutionContext): DBIO[Vector[String]] =
      ResultSetAction[(String, String, String, String)](_.conn.getMetaData().getTables("", "public", null, null)).map {
        ts =>
          ts.filter(_._4.toUpperCase == "TABLE").map(_._3).sorted
      }

    override def localSequences(implicit ec: ExecutionContext): DBIO[Vector[String]] =
      ResultSetAction[(String, String, String, String)](_.conn.getMetaData().getTables("", "public", null, null)).map {
        ts =>
          ts.filter(_._4.toUpperCase == "SEQUENCE").map(_._3).sorted
      }

    override def capabilities = super.capabilities - TestDB.capabilities.jdbcMetaGetFunctions
  }

  val userRepository = new UserRepository(tdb.createDB())
}

@RunWith(classOf[Testkit])
class UserRepositorySpec extends ProfileTest(UserRepositorySpec.tdb) with ZIOSpecDefault {

  import zio._

  println(userRepository.getUserById())

  def spec = suite("HelloWorldSpec")(
    test("sayHello correctly displays output") {
      for {
        _ <- userRepository.getUserById()
        output <- TestConsole.output
      } yield assertTrue(output == Vector("Hello, World!\n"))
    }
  )
}
