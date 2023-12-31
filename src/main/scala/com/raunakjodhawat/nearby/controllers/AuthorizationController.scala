package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.models.user.User
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.*

class AuthorizationController(userRepository: UserRepository) {
  private val log: Logger = LoggerFactory.getLogger(classOf[AuthorizationController])
  def authenticateRequest(headersZIO: ZIO[Any, Throwable, (String, String)]): ZIO[Database, Throwable, Response] = {
    headersZIO.flatMap { headers =>
      val (username, hashedPassword) = headers
      for {
        mayBeUser <- userRepository.getUserByUsername(username)
        response <- mayBeUser match {
          case Some(user) =>
            if (hashedPassword == user.password) {
              ZIO.succeed(Response.ok)
            } else {
              log.error(s"Invalid credentials, for ${user.toString}")
              ZIO.fail(new RuntimeException("Invalid credentials"))
            }
          case None =>
            log.error(s"User not found, for $username")
            ZIO.fail(new RuntimeException("User not found"))
        }
      } yield response
    }
  }
}
