package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.utils.Utils.sendEmail
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._
import com.raunakjodhawat.nearby.models.user.User

import zio._
import zio.http._
import zio.json._

import scala.concurrent.ExecutionContext

class UserController(base_path: Path, userRepository: UserRepository)(implicit
  ec: ExecutionContext
) {
  private val api_path = base_path / "user"
  val api_route = Http.collectZIO[Request] {
    case Method.GET -> Root / long(id) =>
      getUserById(id)
    case req @ Method.POST -> api_path / long(id) =>
      updateUser(req.body, id)
    case Method.GET -> api_path =>
      getAllUsers()
    case req @ Method.POST -> api_path =>
      createUser(req.body)
  }

  private def getAllUsers(): ZIO[Any, Throwable, Response] = {
    println("get all users")
    userRepository
      .getAllUsers()
      .join
      .map(x => Response.json(x.toJson))
  }

  private def getUserById(id: Long): ZIO[Any, Throwable, Response] = {
    userRepository
      .getUserById(id)
      .join
      .map {
        case Some(user) => Response.json(user.toJson)
        case None       => Response.status(Status.NotFound)
      }
  }

  // Used for signup requests
  private def createUser(body: Body): ZIO[Any, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) =>
          userRepository
            .createUser(user)
            .join
            .map { user =>
              sendEmail(user.secret.get, user.username, user.email)
              Response.status(Status.Created)
            }
      }
  }

  private def updateUser(body: Body, id: Long): ZIO[Any, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) =>
          userRepository
            .updateUser(user, id)
            .join
            .map {
              case Some(user) => Response.json(user.toJson)
              case None       => Response.status(Status.NotFound)
            }
      }
  }
}
