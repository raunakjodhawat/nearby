package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import slick.jdbc.PostgresProfile.api._

import zio._
import zio.http._

class VerificationController(base_path: Path, userRepository: UserRepository) {
  private val api_path = base_path / "verify"
  val verify_api_route = Http.collectZIO[Request] { case Method.GET -> api_path / long(id) / secret_key =>
    verifyUser(id, secret_key)
  }

  def verifyUser(id: Long, secret_key: String): ZIO[Database, Throwable, Response] = {
    println("verify user")
    for {
      resultZIO <- for {
        fib <- userRepository
          .verifyUser(id, secret_key)
          .fork
        result <- fib.await
      } yield result match {
        case Exit.Success(_)     => ZIO.succeed(s"User with $id verified")
        case Exit.Failure(cause) => ZIO.failCause(cause)
      }
      response <- resultZIO
    } yield Response.text(response)
  }
}
