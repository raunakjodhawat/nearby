package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation, UserLoginStatus, UserStatus}
import org.junit.runner.RunWith
import slick.jdbc.PostgresProfile.api._
import slick.dbio.DBIO
import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

import java.util.Date
import scala.util.Properties
object UserRepositorySpec {
  val dbZIO = ZIO.attempt(Database.forConfig(Properties.envOrElse("DBPATH", "postgres-test-local")))
  val userRepository = new UserRepository(dbZIO)
  val users = UserRepository.users
  val testUser: User = User(
    Some(1L),
    "username",
    "user123",
    Some("secret"),
    "email",
    Some("phone"),
    Some("address"),
    Some("city"),
    Some("state"),
    Some("country"),
    Some("zip"),
    Some(UserLocation(2.3, 4.5)),
    Some(new java.util.Date()),
    Some(new java.util.Date()),
    Some(UserStatus.ACTIVE),
    Some(UserLoginStatus.LOGGED_IN),
    Some(Avatar.withName("AV_1"))
  )
  def clearDB: ZIO[Any, Throwable, Unit] = {
    for {
      db <- dbZIO
      clearDBFork <- ZIO.fromFuture { ex =>
        {
          db.run(
            DBIO.seq(
              users.schema.dropIfExists,
              users.schema.createIfNotExists
            )
          )
        }
      }.fork
      _ <- clearDBFork.join
      _ <- ZIO.from(db.close())
    } yield ()
  }

  def createAndGetAUser(currentDate: Date): ZIO[Database, Throwable, User] = for {
    _ <- clearDB
    _ <- TestRandom.feedLongs(123456789L, 987654321L, 555555555L)
    uuid <- Random.nextUUID
    _ <- TestRandom.feedUUIDs(uuid)
    _ <- userRepository.createUser(testUser)
    findUserZIO <- userRepository.getUserById(testUser.id.get)
  } yield {
    findUserZIO.copy(
      created_at = Some(currentDate),
      updated_at = Some(currentDate)
    )
  }
}
@RunWith(classOf[ZTestJUnitRunner])
class UserRepositorySpec extends JUnitRunnableSpec {
  import UserRepositorySpec._

  def spec = suite("user repository spec")(
    getAllUsersSuite,
    createUserSuite,
    getUserByIdSuite,
    updateUserSuite
  ).provide(ZLayer.fromZIO(dbZIO)) @@ TestAspect.sequential

  def getAllUsersSuite = suite("Get All Users Spec")(
    test("return an empty array, when db is empty") {
      val zio = for {
        _ <- clearDB
        getAllUserResults <- userRepository.getAllUsers
      } yield getAllUserResults
      assertZIO(zio)(Assertion.hasSize(Assertion.equalTo(0)))
    }
  )

  def createAndGetAUserTest = test("successfully creates a new user, puts it into the database, and retrieves it") {
    val currentDate = new Date()
    assertZIO(createAndGetAUser(currentDate))(
      Assertion.equalTo(
        testUser.copy(
          secret = Some("00000000-075b-4d15-8000-00003ade68b1"),
          created_at = Some(currentDate),
          updated_at = Some(currentDate)
        )
      )
    )
  }
  def createUserSuite = suite("Create a User")(
    createAndGetAUserTest
  )

  def getUserByIdSuite = suite("Get User By Id")(
    test("throws an exception, when user is not available") {
      val zio = for {
        _ <- clearDB
        getUserResult <- userRepository.getUserById(1L)
      } yield getUserResult

      val assertionZIO = zio.fold(
        _ => ZIO.unit,
        _ => ZIO.fail("Assertion failed")
      )
      assertZIO(assertionZIO)(equalTo(ZIO.unit))
    },
    createAndGetAUserTest
  ) @@ TestAspect.timed

  def updateUserSuite = suite("Update User")(
    test("should throw an error, when updating an non-existent user") {
      val zio = for {
        _ <- clearDB
        updateUser <- userRepository.updateUser(testUser, 1L)
      } yield updateUser
      val assertionZIO = zio.fold(
        _ => ZIO.unit,
        _ => ZIO.fail("Assertion failed")
      )
      assertZIO(assertionZIO)(equalTo(ZIO.unit))
    },
    test("should be able to create, get and update a user") {
      val currentDate = new Date()
      for {
        oldUser <- createAndGetAUser(currentDate)
        _ <- userRepository.updateUser(oldUser.copy(phone = Some("89837")), 1L)
        newUser <- userRepository.getUserById(1L)
      } yield {
        zio.test.assert(newUser)(
          Assertion.equalTo(
            oldUser.copy(
              phone = Some("89837"),
              secret = Some("00000000-075b-4d15-8000-00003ade68b1"),
              created_at = newUser.created_at,
              updated_at = newUser.updated_at
            )
          )
        )
      }
    } @@ TestAspect.timeout(10.seconds)
  )
}
