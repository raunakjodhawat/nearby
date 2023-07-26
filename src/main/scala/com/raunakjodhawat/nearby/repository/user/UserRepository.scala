package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UserLoginStatus, UserStatus, UsersTable}
import com.raunakjodhawat.nearby.utils.Utils.secretKey
import slick.jdbc.PostgresProfile.api._
import zio._

import java.util.Date
import scala.concurrent.ExecutionContext

object UserRepository {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}
class UserRepository(dbZIO: ZIO[Any, Throwable, Database])(implicit
  val ex: ExecutionContext
) {
  import UserRepository._
  def getAllUsers: ZIO[Database, Throwable, Seq[UsersTable#TableElementType]] = for {
    db <- dbZIO
    getAllUsersFutureZIO <- ZIO.fromFuture { ex => db.run(users.result) }
  } yield getAllUsersFutureZIO

  def getUserById(id: Long): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] = for {
    db <- dbZIO
    getUserByIdFutureZIO <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === id).result.headOption) }
  } yield getUserByIdFutureZIO

  def createUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] = for {
    db <- dbZIO
    updatedUserFutureZIO <- ZIO.fromFuture { ex =>
      db.run(
        users += user.copy(secret = Some(secretKey()), created_at = Some(new Date()), updated_at = Some(new Date()))
      )
    }
    updationResults <-
      if (updatedUserFutureZIO == 1) ZIO.fromFuture { ex => db.run(users.filter(_.email === user.email).result.head) }
      else ZIO.fail(new Exception("Error creating user"))
  } yield updationResults

  def verifyUser(id: Long, secret: String): ZIO[Database, Throwable, Option[User]] = for {
    db <- dbZIO
    getUserFromFromDB <- ZIO.fromFuture { ex =>
      db.run(users.filter(x => x.id === id && x.secret === secret).result.headOption)
    }
    copyResult <- getUserFromFromDB match {
      case Some(user) => {
        val userCopy = user.copy(secret = None,
                                 updated_at = Some(new Date()),
                                 status = Some(UserStatus.ACTIVE),
                                 login_status = Some(UserLoginStatus.LOGGED_IN)
        )
        ZIO.fromFuture { ex =>
          db.run(users.filter(_.id === id).update(userCopy)).map(i => if (i == 1) Some(userCopy) else None)
        }
      }
      case None => ZIO.fail(new Exception("Invalid secret key"))
    }
  } yield copyResult

  def updateUser(user: User, id: Long): ZIO[Database, Throwable, User] = {
    val userCopy = user.copy(id = Some(id), updated_at = Some(new Date()))
    for {
      db <- dbZIO
      updateResultCount <- ZIO.fromFuture { ex => db.run(users.filter(_.id === id).update(userCopy)) }
      updateResult <-
        if (updateResultCount == 1) ZIO.succeed(userCopy)
        else ZIO.fail(new Exception("failed to update the user"))
    } yield updateResult
  }
}
