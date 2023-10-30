package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UsersTable}
import com.raunakjodhawat.nearby.utils.Utils.{generateSalt, secretKey}
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api.*

import java.util.Date
import zio.*

object UserRepository {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
}
class UserRepository(dbZIO: ZIO[Any, Throwable, Database]) {
  import UserRepository._

  val log: Logger = LoggerFactory.getLogger(classOf[UserRepository])
  def getUserByUsername(username: String): ZIO[Database, Throwable, Option[UsersTable#TableElementType]] = for {
    db <- dbZIO
    user <- ZIO.fromFuture { ex => db.run(users.filter(_.username === username).result.headOption) }
    _ <- ZIO.from(db.close())
  } yield user
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

  def createUser(user: User): ZIO[Database, Throwable, Unit] =
    for {
      db <- dbZIO
      updatedUserFutureZIO <- ZIO.fromFuture { ex =>
        db.run(
          users += user.copy(created_at = Some(new Date()), updated_at = Some(new Date()))
        )
      }
      _ <- ZIO.from(db.close())
    } yield {
      if (updatedUserFutureZIO == 1) ZIO.succeed(())
      else ZIO.fail(new Exception("Error creating user"))
    }

  def verifyUser(id: Long, newSecret: String): ZIO[Database, Throwable, String] = for {
    db <- dbZIO
    getUserFromFromDB <- ZIO.fromFuture { ex =>
      db.run(users.filter(x => x.id === id && x.secret === newSecret).result.headOption)
    }
    copyResultCount <- getUserFromFromDB match {
      case Some(user) => {
        val userCopy = user.copy(updated_at = Some(new Date()), activationComplete = true)
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

  // if incoming user object has a property, use that otherwise use the one from the database
  private def modifyIfIncomingValueExists[T](newValue: Option[T], dbValue: Option[T]): Option[T] = newValue match {
    case Some(v) => Some(v)
    case None    => dbValue
  }
  def updateUser(incomingUser: User, id: Long): ZIO[Database, Throwable, User] = {
    for {
      db <- dbZIO
      userFromDB <- getUserById(id)
      userCopy = incomingUser.copy(
        id = id,
        secret = incomingUser.secret,
        phone = modifyIfIncomingValueExists(incomingUser.phone, userFromDB.phone),
        location = modifyIfIncomingValueExists(incomingUser.location, userFromDB.location),
        created_at = userFromDB.created_at,
        updated_at = Some(new Date()),
        activationComplete = incomingUser.activationComplete,
        avatar = modifyIfIncomingValueExists(incomingUser.avatar, userFromDB.avatar)
      )
      updateResultCount <- ZIO.fromFuture { ex => db.run(users.filter(_.id === id).update(userCopy)) }
      updateResult <-
        if (updateResultCount == 1) ZIO.succeed(userCopy)
        else ZIO.fail(new Exception("failed to update the user"))
      _ <- ZIO.from(db.close())
    } yield updateResult
  }
}
