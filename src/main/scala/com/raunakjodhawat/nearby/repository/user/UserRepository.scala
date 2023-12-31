package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UsersTable}
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
  def getUserById(id: Long): ZIO[Database, Throwable, UsersTable#TableElementType] = (for {
    db <- dbZIO
    getUserByIdFutureZIO <- ZIO.fromFuture { ex => db.run(users.filter(x => x.id === id).result.headOption) }
    _ <- ZIO.from(db.close())
  } yield getUserByIdFutureZIO).flatMap {
    case Some(user) => ZIO.succeed(user)
    case None       => ZIO.fail(new Exception("unable to find the user"))
  }

  def getOrCreateUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] = for {
    db <- dbZIO
    userFromDB <- getUserByUsername(user.username)
    userFromDBOrCreated <- userFromDB match {
      case Some(user) => ZIO.succeed(user)
      case None       => createUser(user)
    }
    _ <- ZIO.from(db.close())
  } yield userFromDBOrCreated
  def createUser(user: User): ZIO[Database, Throwable, UsersTable#TableElementType] =
    for {
      db <- dbZIO
      updatedUserFutureZIO <- ZIO.fromFuture { ex =>
        db.run(
          users += user.copy(created_at = Some(new Date()), updated_at = Some(new Date()))
        )
      }
      user <-
        if (updatedUserFutureZIO == 1) ZIO.succeed(user)
        else ZIO.fail(new Exception("Error creating user"))
      _ <- ZIO.from(db.close())
    } yield user

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
