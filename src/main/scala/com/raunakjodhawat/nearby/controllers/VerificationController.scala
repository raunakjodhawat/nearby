package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._

import zio._
import zio.http._
import zio.json._

import scala.concurrent.ExecutionContext

class VerificationController(basePath: Path, userRepository: UserRepository)(implicit
  ec: ExecutionContext
) {
  private val api_path = basePath / "verify"
  val verify_api_route = Http.collectZIO[Request] { case Method.GET -> api_path / long(id) / secret_key =>
    verifyUser(id, secret_key)
  }

  private def verifyUser(id: Long, secret_key: String): ZIO[Any, Throwable, Response] = {
    userRepository
      .verifyUser(id, secret_key)
      .join
      .map {
        case Some(user) => Response.json(user.toJson)
        case None       => Response.status(Status.NotFound)
      }
  }
}
