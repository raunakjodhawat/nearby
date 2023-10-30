package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.models.user.LoginResponse
import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder.*
import com.raunakjodhawat.nearby.models.user.{LoginUser, User, UsersTable}
import com.raunakjodhawat.nearby.utils.Utils.verifyPassword
import io.circe.*
import io.circe.syntax.*
import io.circe.parser.decode
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api.*
import zio.*
import zio.http.*

import java.util.Base64

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

  def loginUser(body: Body): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(decode[LoginUser])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(incomingLoginUser) =>
          for {
            user <- userRepository
              .getOrCreateUser(incomingLoginUser.toUser)
            authToken <- createAuthToken(
              incomingLoginUser.username,
              incomingLoginUser.password,
              user.secret,
              user.password
            )
          } yield Response.json(authToken.asJson.toString())
      }
  }
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

  private def createAuthToken(
    username: String,
    password: String,
    secret: String,
    hashedPassword: String
  ): ZIO[Any, Throwable, LoginResponse] = {
    if (verifyPassword(password, secret, hashedPassword)) {
      val token = s"$username:$hashedPassword"
      val encodedToken = Base64.getEncoder.encodeToString(token.getBytes())
      ZIO.succeed(LoginResponse(s"Basic $encodedToken"))
    } else {
      ZIO.fail(new Exception("Invalid Password"))
    }
  }
}
