package com.raunakjodhawat.nearby.utils

import zio.*
import zio.http.Headers

import java.security.{MessageDigest, SecureRandom}
import java.util.Base64
object Utils {

  def decodeAuthorizationHeader(headers: Headers): ZIO[Any, Throwable, (String, String)] = {
    headers.get("Authorization") match {
      case Some(header) =>
        val base64Credentials = header.split(" ")
        if (base64Credentials.length != 2) {
          ZIO.fail(new RuntimeException("Invalid authorization header"))
        } else {
          val credentials = new String(java.util.Base64.getDecoder.decode(base64Credentials(1))).split(":")
          if (credentials.length != 2) {
            ZIO.fail(new RuntimeException("Invalid authorization header"))
          } else {
            ZIO.succeed((credentials(0), credentials(1).trim))
          }
        }
      case None => ZIO.fail(new RuntimeException("Authorization header not found"))
    }
  }
  def generateSalt: String = {
    val secureRandom = new SecureRandom()
    val salt = new Array[Byte](16)
    secureRandom.nextBytes(salt)
    Base64.getEncoder.encodeToString(salt)
  }

  def hashPassword(password: String, salt: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(Base64.getDecoder.decode(salt))
    val hashedPassword = md.digest(password.getBytes("UTF-8"))
    Base64.getEncoder.encodeToString(hashedPassword)
  }

  def verifyPassword(providedPassword: String, salt: String, hashedPassword: String): Boolean = {
    val providedPasswordHash = hashPassword(providedPassword, salt)
    providedPasswordHash == hashedPassword
  }
}
