package com.raunakjodhawat.nearby.controllers

class UserController {
  def getSalt(username: String): Option[String] = ???
  def loginUser(username: String, password: String): Option[String] = ???
}
