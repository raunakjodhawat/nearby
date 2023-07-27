package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation, UserLoginStatus, UserStatus, UsersTable}
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.PostgresProfile.api._
import slick.dbio.DBIO
import zio._
import zio.test.{test, _}
import zio.test.junit.JUnitRunnableSpec

import java.util.{Date, UUID}
import scala.concurrent.Future
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
    Some(Avatar.AV_1)
  )
  trait Environment {

    val runtime = Runtime.default
  }

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
    } yield ()
  }
}
class UserRepositorySpec extends JUnitRunnableSpec {
  import UserRepositorySpec._

  def spec = createUserSpec.provide(ZLayer.fromZIO(dbZIO))

  def getAllUsersSpec = suite("Get All Users Spec")(
    test("return an empty array, when db is empty") {
      val zio = for {
        _ <- clearDB
        getAllUserResults <- userRepository.getAllUsers
      } yield getAllUserResults
      assertZIO(zio)(Assertion.hasSize(Assertion.equalTo(0)))
    }
  )

  def createUserSpec = suite("Create a User")(
    test("successfully creates a new user, and puts it into the database") {
      val currentDate = new Date()
      println(currentDate.toString)
      val zio = for {
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
      assertZIO(zio)(
        Assertion.equalTo(
          testUser.copy(
            secret = Some("00000000-075b-4d15-8000-00003ade68b1"),
            created_at = Some(currentDate),
            updated_at = Some(currentDate)
          )
        )
      )
    }
  )

//  "getUserById, when UserRepository is empty" should {
//    "return empty User" in new Environment {
//      val fiberFuture = for {
//        _ <- ZIO.fromFuture(_ => clearDB)
//        repoFuture <- userRepository.getUserById(1L)
//      } yield repoFuture
//
//      val fiber = fiberFuture.map {
//        case None => assert(true)
//        case _    => fail("User should not be present")
//      }
//      Unsafe.unsafe { implicit rts =>
//        {
//          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
//        }
//      }
//    }
//  }
//
//  "getAllUser, when UserRepository is empty" should {
//    "return empty Seq[User]" in new Environment {
//      val fiberFuture = for {
//        _ <- ZIO.fromFuture(_ => clearDB)
//        repoFuture <- userRepository
//          .getAllUsers()
//          .join
//      } yield repoFuture
//
//      val fiber = fiberFuture.map {
//        case x: Seq[User] if x.isEmpty => assert(true)
//        case _                         => fail("User list should be empty")
//      }
//      Unsafe.unsafe { implicit rts =>
//        {
//          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
//        }
//      }
//    }
//  }
//
//  "createUser" should {
//    "be able to create a new user" in new Environment {
//      val fiberFuture = for {
//        _ <- ZIO.fromFuture(_ => clearDB)
//        repoFuture <- userRepository
//          .createUser(testUser)
//          .join
//      } yield repoFuture
//
//      val fiber = fiberFuture.map {
//        case x: Int if x == 1 => assert(true)
//        case _                => fail("Unable to create the user")
//      }
//      Unsafe.unsafe { implicit rts =>
//        {
//          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
//        }
//      }
//    }
//  }
//
//  "updateUser" should {
//    "be able to update a existing user" in new Environment {
//      val newTestUser = testUser.copy(username = "updatedUsername")
//      val fiberFuture = for {
//        _ <- ZIO.fromFuture(_ => clearDB)
//        _ <- userRepository
//          .createUser(testUser)
//          .join
//        _ <- userRepository.getUserById(1L).join
//        updatedUser <- userRepository
//          .updateUser(newTestUser, 1L)
//          .join
//      } yield updatedUser
//      val fiber = fiberFuture.map {
//        case Some(user: User) => assert(user.username == newTestUser.username)
//        case _                => fail("Unable to update the user")
//      }
//      Unsafe.unsafe { implicit rts =>
//        {
//          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
//        }
//      }
//    }
//    "throw exception, when updating a non existent user" in new Environment {
//      val newTestUser = testUser.copy(username = "updatedUsername")
//      val fiberFuture = for {
//        _ <- ZIO.fromFuture(_ => clearDB)
//        updatedUser <- userRepository
//          .updateUser(newTestUser, 1L)
//          .join
//      } yield updatedUser
//      val fiber = fiberFuture.map {
//        case Some(user: User) => fail("User should not be returned")
//        case _                => assert(true)
//      }
//      Unsafe.unsafe { implicit rts =>
//        {
//          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
//        }
//      }
//    }
//  }
}
