package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import com.raunakjodhawat.nearby.models.user.UserLoginStatus.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus.UserStatus
import com.raunakjodhawat.nearby.models.user.{
  User,
  UserAlreadyExistsException,
  UserDoesNotExistException,
  UserLocation,
  UsersTable
}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import zio.{Fiber, ZIO}
import zio.json.{DeriveJsonDecoder, JsonDecoder}

import java.util.Date
import scala.util.{Failure, Success, Try}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepository(db: PostgresProfile.backend.Database)(implicit
  val ex: ExecutionContext
) {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
  def getAllUsers(): Fiber[Throwable, Seq[UsersTable#TableElementType]] = Fiber.fromFuture(db.run(users.result))

  def getUserById(id: Long): Fiber[Throwable, Option[UsersTable#TableElementType]] = {
    Fiber.fromFuture(db.run(users.filter(x => x.id === id).result.headOption))
  }
  def createUser(user: User): Fiber[Throwable, Int] = Fiber.fromFuture(db.run(users += user))

  def updateUser(user: User): ZIO[Any, UserDoesNotExistException, User] = {
    val userCopy = user.copy(id = user.id, updated_at = Some(new Date()))
    Try(db.run(users.filter(_.id === user.id).update(userCopy))).map(_ => user) match {
      case Success(_) => ZIO.succeed[User](userCopy)
      case Failure(_) => ZIO.fail(new UserDoesNotExistException(user.id))
    }
  }
}
