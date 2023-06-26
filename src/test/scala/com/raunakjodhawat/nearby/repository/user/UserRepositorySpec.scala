package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation, UserLoginStatus, UserStatus, UsersTable}
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO

import zio._

import scala.concurrent.Future
object UserRepositorySpec {
  val db = Database.forConfig("postgres-test")
  trait Environment {
    println("trait is initialized")
    val testUser: User = User(
      Some(1L),
      "username",
      "user123",
      "salt",
      "email",
      "phone",
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
    val userRepository = new UserRepository(db)
    val runtime = Runtime.default
  }

  def clearDB: Future[Unit] = {
    val users = TableQuery[UsersTable]
    db.run(
      DBIO.seq(
        users.schema.dropIfExists,
        users.schema.createIfNotExists
      )
    )
  }
}
class UserRepositorySpec extends AnyWordSpec {
  import UserRepositorySpec._

  "getUserById, when UserRepository is empty" should {
    "return empty User" in new Environment {
      val fibers = for {
        _ <- ZIO.fromFuture(_ => clearDB)
        mayBeUser <- userRepository.getUserById(1L).join
      } yield mayBeUser

      val fiber = fibers.map {
        case None => assert(true)
        case _    => fail("User should not be present")
      }
      Unsafe.unsafe { implicit rts =>
        {
          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
        }
      }
    }
  }

  "getAllUser, when UserRepository is empty" should {
    "return empty Seq[User]" in new Environment {
      val fibers = for {
        _ <- ZIO.fromFuture(_ => clearDB)
        mayBeUser <- userRepository
          .getAllUsers()
          .join
      } yield mayBeUser

      val fiber = fibers.map {
        case x: Seq[User] if x.isEmpty => assert(true)
        case _                         => fail("User list should be empty")
      }
      Unsafe.unsafe { implicit rts =>
        {
          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
        }
      }
    }
  }

//  "UserRepository" should {
////    "be able to create a new user" in new Environment {
////      val fiber = userRepository
////        .createUser(testUser)
////        .join
////        .map {
////          case x: Int if x == 1 => assert(true)
////          case _                => fail("Unable to create the user")
////        }
////      Unsafe.unsafe { implicit rts =>
////        {
////          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
////        }
////      }
////    }
//
////    "be able to query for a user" in new Environment {
////      val fiber =
////        userRepository.createUser(testUser).join.flatMap(_ => userRepository.getUserById(testUser.id.get).join).map {
////          case Some(user) => assert(user == testUser)
////          case None       => fail("User should be present")
////        }
////      Unsafe.unsafe { implicit rts =>
////        {
////          runtime.unsafe.run(fiber).getOrThrowFiberFailure()
////        }
////      }
////    }
//  }
}
