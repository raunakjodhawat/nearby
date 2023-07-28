package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UserLoginStatus, UserStatus, UsersTable}
import com.raunakjodhawat.nearby.utils.Utils.secretKey

import slick.jdbc.PostgresProfile.api._

import java.util.Date

import zio._

object UserRepository {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}
class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  import UserRepository._
  def getAllUsers: ZIO[Database, Throwable, Seq[UsersTable#TableElementType]] = for {
    db <- dbZIO
    getAllUsersFutureZIO <- ZIO.fromFuture { ex => db.run(users.result) }
    _ <- ZIO.from(db.close())
  } yield getAllUsersFutureZIO

  def getUserById(id: Long): ZIO[Database, Throwable, UsersTable#TableElementType] = (for {
    db <- dbZIO
    getUserByIdFutureZIO <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === id).result.headOption) }
    _ <- ZIO.from(db.close())
  } yield getUserByIdFutureZIO).flatMap {
    case Some(user) => ZIO.succeed(user)
    case None       => ZIO.fail(new Exception("unable to find the user"))
  }

  def createUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] =
    for {
      db <- dbZIO
      secret <- secretKey
      updatedUserFutureZIO <- ZIO.fromFuture { ex =>
        db.run(
          users += user.copy(secret = Some(secret.toString),
                             created_at = Some(new Date()),
                             updated_at = Some(new Date())
          )
        )
      }
      updationResults <-
        if (updatedUserFutureZIO == 1) ZIO.fromFuture { ex => db.run(users.filter(_.email === user.email).result.head) }
        else ZIO.fail(new Exception("Error creating user"))
      _ <- ZIO.from(db.close())
    } yield updationResults

  def verifyUser(id: Long, secret: String): ZIO[Database, Throwable, String] = for {
    db <- dbZIO
    getUserFromFromDB <- ZIO.fromFuture { ex =>
      db.run(users.filter(x => x.id === id && x.secret === secret).result.headOption)
    }
    copyResultCount <- getUserFromFromDB match {
      case Some(user) => {
        val userCopy = user.copy(secret = None,
                                 updated_at = Some(new Date()),
                                 status = Some(UserStatus.ACTIVE),
                                 login_status = Some(UserLoginStatus.LOGGED_IN)
        )
        ZIO.fromFuture { ex =>
          db.run(users.filter(_.id === id).update(userCopy))
        }
      }
      case None => ZIO.succeed(0)
    }
    copyResult <-
      if (copyResultCount == 1) ZIO.succeed("User update Success")
      else ZIO.fail(new Exception("failed to verifyUser the user"))
    _ <- ZIO.from(db.close())
  } yield copyResult

  def updateUser(user: User, id: Long, updateDate: Date): ZIO[Database, Throwable, User] = {
    val userCopy = user.copy(id = Some(id), updated_at = Some(updateDate))
    for {
      db <- dbZIO
      updateResultCount <- ZIO.fromFuture { ex => db.run(users.filter(_.id === id).update(userCopy)) }
      updateResult <-
        if (updateResultCount == 1) ZIO.succeed(println("all done")) *> ZIO.succeed(userCopy)
        else ZIO.succeed(println("all done")) *> ZIO.fail(new Exception("failed to update the user"))
      _ <- ZIO.from(db.close())
    } yield updateResult
  }
}
