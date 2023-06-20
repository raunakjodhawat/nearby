package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.models.user.User
import zio.ZIO

class UserController {
  def getSalt(username: String): Option[String] = ???
  def loginUser(username: String, password: String): Option[String] = ???
}
