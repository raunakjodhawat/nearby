package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder.*
import com.raunakjodhawat.nearby.models.user.{LoginUser, User}
import io.circe.*
import io.circe.syntax.*
import io.circe.parser.decode
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.*

class UserController(userRepository: UserRepository) {
  val log: Logger = LoggerFactory.getLogger(classOf[UserController])
  def getAllUsers: ZIO[Database, Throwable, Response] = for {
    resultZIO <- for {
      fib <- userRepository.getAllUsers.fork
      res <- fib.await
    } yield res match {
      case Exit.Success(v)     => ZIO.succeed(v)
      case Exit.Failure(cause) => ZIO.failCause(cause)
    }
    result <- resultZIO
  } yield if (result.isEmpty) Response.status(Status.NoContent) else Response.json(result.asJson.toString())

  def getUserById(id: Long): ZIO[Database, Throwable, Response] = for {
    resultZIO <- for {
      fib <- userRepository.getUserById(id).fork
      res <- fib.await
    } yield res match {
      case Exit.Success(v)     => ZIO.succeed(v)
      case Exit.Failure(cause) => ZIO.failCause(cause)
    }
    result <- resultZIO
  } yield Response.json(result.asJson.toString())

  // Used for signup requests
  def createUser(body: Body): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[LoginUser])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(loginUser) =>
          for {
            resultZIO <- for {
              fib <- userRepository
                .createUser(loginUser.toUser)
                .fork
              res <- fib.await
            } yield res match {
              case Exit.Success(v)     => ZIO.succeed(v)
              case Exit.Failure(cause) => ZIO.failCause(cause)
            }
            _ <- resultZIO
          } yield Response.status(Status.Created)
      }
  }

  def updateUser(body: Body, id: Long): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) => {
          for {
            resultZIO <- for {
              fib <- userRepository
                .updateUser(user, id)
                .fork
              res <- fib.await
            } yield res match {
              case Exit.Success(v)     => ZIO.succeed(v)
              case Exit.Failure(cause) => ZIO.failCause(cause)
            }
            user <- resultZIO
          } yield Response.json(user.asJson.toString())
        }
      }
  }
}
