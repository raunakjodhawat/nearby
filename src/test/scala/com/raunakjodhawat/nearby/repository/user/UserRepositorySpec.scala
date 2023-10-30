package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.{
  customAssertZIO,
  testUser,
  test_comments,
  test_date,
  test_dbZIO,
  test_posts,
  test_users
}
import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation}
import org.junit.runner.RunWith
import slick.jdbc
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

object UserRepositorySpec {
  val userRepository = new UserRepository(test_dbZIO)
  val users = UserRepository.users

  def clearDB(): ZIO[Any, Throwable, jdbc.PostgresProfile.backend.JdbcDatabaseDef] = {
    for {
      db <- test_dbZIO
      dbCreationFuture <- ZIO.fromFuture { ex =>
        {
          db.run(
            DBIO.seq(
              test_comments.schema.dropIfExists,
              test_posts.schema.dropIfExists,
              test_users.schema.dropIfExists,
              test_users.schema.create,
              test_posts.schema.create,
              test_comments.schema.create
            )
          )
        }
      }.fork
      _ <- dbCreationFuture.join
      _ <- ZIO.from(db.close())
    } yield db
  }
  def createAndGetAUser(): ZIO[Database, Throwable, User] = {
    val user = testUser()
    for {
      db <- clearDB()
      _ <- TestRandom.feedLongs(123456789L, 987654321L, 555555555L)
      uuid <- Random.nextUUID
      _ <- TestRandom.feedUUIDs(uuid)
      _ <- userRepository.createUser(user)
      findUserZIO <- userRepository.getUserById(user.id)
      _ <- ZIO.from(db.close())
    } yield {
      findUserZIO.copy(
        secret = "00000000-075b-4d15-8000-00003ade68b1",
        created_at = Some(test_date),
        updated_at = Some(test_date)
      )
    }
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
  ).provide(ZLayer.fromZIO(test_dbZIO)) @@ TestAspect.sequential @@ TestAspect.timed

  def getAllUsersSuite = suite("Get All Users Spec")(
    test("return an empty array, when db is empty") {
      val zio = for {
        _ <- clearDB()
        getAllUserResults <- userRepository.getAllUsers
      } yield getAllUserResults
      assertZIO(zio)(Assertion.hasSize(Assertion.equalTo(0)))
    }
  )

  def createAndGetAUserTest = test("successfully creates a new user, puts it into the database, and retrieves it") {
    assertZIO(createAndGetAUser())(
      Assertion.equalTo(
        testUser().copy(
          secret = "00000000-075b-4d15-8000-00003ade68b1"
        )
      )
    )
  }
  def createUserSuite = suite("Create a User")(createAndGetAUserTest)

  def getUserByIdSuite = suite("Get User By Id")(
    test("throws an exception, when user is not available") {
      val zio =
        for {
          db <- clearDB()
          getUserResult <- userRepository.getUserById(1L)
          _ <- ZIO.from(db.close())
        } yield getUserResult

      val assertionZIO = zio.fold(
        _ => ZIO.unit,
        _ => ZIO.fail("Assertion failed")
      )
      assertZIO(assertionZIO)(equalTo(ZIO.unit))
    },
    createAndGetAUserTest
  )

  def updateUserSuite = suite("Update User")(
    test("should throw an error, when updating an non-existent user") {
      val zio =
        for {
          db <- clearDB()
          updateUser <- userRepository.updateUser(testUser(), 1L)
          _ <- ZIO.from(db.close())
        } yield updateUser
      customAssertZIO(zio)
    },
    test("should be able to create, get and update a user") {
      for {
        db <- clearDB()
        oldUser <- createAndGetAUser()
        _ <- userRepository.updateUser(oldUser.copy(phone = Some("89837")), 1L)
        newUser <- userRepository.getUserById(1L)
        _ <- ZIO.from(db.close())
      } yield {
        zio.test.assert(newUser)(
          Assertion.equalTo(
            oldUser.copy(
              phone = Some("89837"),
              secret = "00000000-075b-4d15-8000-00003ade68b1",
              created_at = newUser.created_at,
              updated_at = newUser.updated_at
            )
          )
        )
      }
    } @@ TestAspect.timeout(10.seconds)
  ) @@ TestAspect.sequential
}
