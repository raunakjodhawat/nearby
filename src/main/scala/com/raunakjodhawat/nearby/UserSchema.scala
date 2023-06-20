package com.raunakjodhawat.nearby

import com.raunakjodhawat.nearby.models.user.User
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
class UserSchema {
  def apply() = {
    val users = TableQuery[User]
    val db = Database.forConfig("database")
    db.run(
      DBIO.seq(
        users.schema.create
      )
    )
  }
}
