package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UsersTable}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import zio.Fiber

import java.util.Date
import scala.concurrent.ExecutionContext

class UserRepository(db: Database)(implicit
  val ex: ExecutionContext
) {
  val users: TableQuery[UsersTable] = TableQuery[UsersTable]
  def getAllUsers(): Fiber[Throwable, Seq[UsersTable#TableElementType]] = Fiber.fromFuture(db.run(users.result))

  def getUserById(id: Long): Fiber[Throwable, Option[UsersTable#TableElementType]] = {
    Fiber.fromFuture(db.run(users.filter(x => x.id === id).result.headOption))
  }
  def createUser(user: User): Fiber[Throwable, Int] = Fiber.fromFuture(db.run(users += user))

  def updateUser(user: User, id: Long): Fiber[Throwable, Option[User]] = {
    val userCopy = user.copy(id = Some(id), updated_at = Some(new Date()))
    Fiber.fromFuture(db.run(users.filter(_.id === id).update(userCopy))).map(i => if (i == 1) Some(userCopy) else None)
  }
}
