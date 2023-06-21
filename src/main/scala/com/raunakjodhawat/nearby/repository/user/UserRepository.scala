package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UserAlreadyExistsException, UsersTable}
import slick.jdbc.PostgresProfile

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.CanBeQueryCondition.BooleanCanBeQueryCondition
import zio.ZIO

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object Connection {
  val db = Database.forConfig("postgres")
}

class UserRepository(db: PostgresProfile.backend.Database, users: TableQuery[UsersTable] = TableQuery[UsersTable])(
  implicit val ex: ExecutionContext
) {
  def getAllUsers(): ZIO[Any, Throwable, Seq[UsersTable#TableElementType]] = ZIO.from { db.run(users.result) }

  def getUserById(id: Long): ZIO[Any, Throwable, Option[UsersTable#TableElementType]] = ZIO.from {
    db.run(users.filter(x => x.id === id).result.headOption)
  }
  def createUser(user: User): ZIO[Any, UserAlreadyExistsException, User] =
    Try(db.run(users += user)).map(_ => user) match {
      case Success(_) => ZIO.succeed[User](user)
      case Failure(_) => ZIO.fail(new UserAlreadyExistsException(user.id))
    }
}
