package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UserLoginStatus, UserStatus, UsersTable}
import com.raunakjodhawat.nearby.utils.Utils.secretKey
import slick.jdbc.PostgresProfile.api._
import zio._

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
  def createUser(user: User): Fiber[Throwable, UsersTable#TableElementType] = Fiber.fromFuture(
    db.run(
      users += user.copy(secret = Some(secretKey()), created_at = Some(new Date()), updated_at = Some(new Date()))
    ).flatMap {
      case 1 => db.run(users.filter(_.email === user.email).result.head)
      case _ => throw new Exception("Error creating user")
    }
  )

  def verifyUser(id: Long, secret: String): Fiber[Throwable, Option[User]] = Fiber.fromFuture {
    db.run(users.filter(x => x.id === id && x.secret === secret).result.headOption).flatMap {
      case Some(user) => {
        val userCopy = user.copy(secret = None,
                                 updated_at = Some(new Date()),
                                 status = Some(UserStatus.ACTIVE),
                                 login_status = Some(UserLoginStatus.LOGGED_IN)
        )
        db.run(users.filter(_.id === id).update(userCopy)).map(i => if (i == 1) Some(userCopy) else None)
      }
      case None => throw new Exception("Invalid secret key")
    }
  }

  def updateUser(user: User, id: Long): Fiber[Throwable, Option[User]] = {
    val userCopy = user.copy(id = Some(id), updated_at = Some(new Date()))
    Fiber.fromFuture(db.run(users.filter(_.id === id).update(userCopy))).map(i => if (i == 1) Some(userCopy) else None)
  }
}
