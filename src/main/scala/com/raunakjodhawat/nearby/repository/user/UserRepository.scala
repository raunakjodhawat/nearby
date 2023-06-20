package com.raunakjodhawat.nearby.repository.user

import com.raunakjodhawat.nearby.models.user.{User, UsersTable}
import slick.jdbc.PostgresProfile
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
object Connection {
  val db = Database.forConfig("postgres")
}

class UserRepository(db: PostgresProfile.backend.Database)(implicit val ex: ExecutionContext) {

  def getAllUsers(): Future[Seq[User]] = db.run(TableQuery[UsersTable].result)

  def getUserById(id: Long): Future[Option[User]] = db.run(TableQuery[UsersTable].filter(_.id == id).result.headOption)

  def createIfUserDoesNotExist(user: User): Future[User] = {
    getUserById(user.id).flatMap {
      case Some(user) => Future.successful(user)
      case None       => createUser(user)
    }
  }
  def createUser(user: User): Future[User] = db
    .run(TableQuery[UsersTable].returning(TableQuery[UsersTable].map(_.id)) += user)
    .map(id => user.copy(id = id))
}
