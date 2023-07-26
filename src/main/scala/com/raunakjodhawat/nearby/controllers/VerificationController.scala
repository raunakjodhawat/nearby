package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._

import zio._
import zio.http._
import zio.json._

import scala.concurrent.ExecutionContext

class VerificationController(base_path: Path, userRepository: UserRepository)(implicit
  ec: ExecutionContext
) {
  private val api_path = base_path / "verify"
  val verify_api_route = Http.collectZIO[Request] {
    case Method.GET -> api_path / long(id) =>
      verifyUser(1, "secret_key")
    case _ => {
      println("reachint here")
      ZIO.succeed(Response.text("Invalid request"))
    }
  }

  private def verifyUser(id: Long, secret_key: String): ZIO[Any, Throwable, Response] = {
    println("verify user")
    userRepository
      .verifyUser(id, secret_key)
      .join
      .map {
        case Some(user) => Response.json(user.toJson)
        case None       => Response.status(Status.NotFound)
      }
  }
}
