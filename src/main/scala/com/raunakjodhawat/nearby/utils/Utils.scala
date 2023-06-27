package com.raunakjodhawat.nearby.utils

import java.util.UUID.randomUUID
object Utils {
  // credits: https://stackoverflow.com/a/32445372
  private val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  def secretKey(): String = randomUUID.toString

  // credits: https://stackoverflow.com/a/32445372
  def isValidEmail(email: String): Boolean = email match {
    case e if e.trim.isEmpty                           => false
    case e if emailRegex.findFirstMatchIn(e).isDefined => true
    case _                                             => false
  }

  def isValidUsername(username: String): Boolean = username.length > 5 && username.length < 32
  def isValidPassword(password: String): Boolean = password.length > 5 && password.length < 32

}
