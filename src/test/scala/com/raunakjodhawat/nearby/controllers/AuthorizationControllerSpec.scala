package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.{clearDB, customAssertZIO}

import com.raunakjodhawat.nearby.models.user.LoginUser
import com.raunakjodhawat.nearby.utils.Utils.decodeAuthorizationHeader
import org.junit.runner.RunWith

import slick.jdbc
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.Headers
import zio.test.*
import zio.test.Assertion.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import scala.util.Properties

object AuthorizationControllerSpec {

  val test_dbZIO: Task[PostgresProfile.backend.JdbcDatabaseDef] =
    ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val userRepository = new UserRepository(test_dbZIO)
  val loginUser: LoginUser = LoginUser(
    username = "username",
    password = "password"
  )
  val ac = new AuthorizationController(userRepository)

  def createAUser(): ZIO[Database, Throwable, Unit] = for {
    db <- clearDB()
    _ <- TestRandom.feedLongs(123456789L, 987654321L, 555555555L)
    uuid <- Random.nextUUID
    _ <- TestRandom.feedUUIDs(uuid)
    _ <- userRepository.createUser(loginUser.toUser)
    _ <- ZIO.from(db.close())
  } yield ()
}
@RunWith(classOf[ZTestJUnitRunner])
class AuthorizationControllerSpec extends JUnitRunnableSpec {
  import AuthorizationControllerSpec._

  def spec =
    suite("Authorization Controller")(
      test("Authorization fails, when username is non-existent in the db") {
        val incomingHeader = Headers(("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQK"))
        val zio = for {
          _ <- clearDB()
          header = decodeAuthorizationHeader(incomingHeader)
          result <- ac.authenticateUser(header)
        } yield result
        customAssertZIO(zio)
      },
      test("Authorization fails, when username is exists, but password is incorrect") {
        val incomingHeader = Headers(("Authorization", "Basic somerandomstring"))
        val zio = for {
          _ <- clearDB()
          _ <- createAUser()
          header = decodeAuthorizationHeader(incomingHeader)
          result <- ac.authenticateUser(header)
        } yield result
        customAssertZIO(zio)
      },
      test("Authorization succeeds, when username and password are both correct") {
        val incomingHeader = Headers(("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQK"))
        for {
          _ <- clearDB()
          _ <- createAUser()
          header = decodeAuthorizationHeader(incomingHeader)
          result <- ac.authenticateUser(header)
        } yield {
          assert(result.status.code)(equalTo(200))
        }
      }
    ).provide(ZLayer.fromZIO(test_dbZIO)) @@ TestAspect.sequential @@ TestAspect.timed
}
