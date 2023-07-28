package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.repository.user.UserRepository
import com.raunakjodhawat.nearby.utils.Utils.sendEmail
import com.raunakjodhawat.nearby.models.user.JsonEncoderDecoder._
import com.raunakjodhawat.nearby.models.user.User
import slick.jdbc.PostgresProfile.api._
import zio._
import zio.http._
import zio.json._

import java.util.Date

class UserController(userRepository: UserRepository) {
  def getAllUsers: ZIO[Database, Throwable, Response] = for {
    resultZIO <- for {
      fib <- userRepository.getAllUsers.fork
      res <- fib.await
    } yield res match {
      case Exit.Success(v)     => ZIO.succeed(v)
      case Exit.Failure(cause) => ZIO.failCause(cause)
    }
    result <- resultZIO
  } yield Response.json(result.toJson)

  def getUserById(id: Long): ZIO[Database, Throwable, Response] = for {
    resultZIO <- for {
      fib <- userRepository.getUserById(id).fork
      res <- fib.await
    } yield res match {
      case Exit.Success(v)     => ZIO.succeed(v)
      case Exit.Failure(cause) => ZIO.failCause(cause)
    }
    result <- resultZIO
  } yield Response.json(result.toJson)

  // Used for signup requests
  def createUser(body: Body): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) =>
          for {
            resultZIO <- for {
              fib <- userRepository
                .createUser(user)
                .fork
              res <- fib.await
            } yield res match {
              case Exit.Success(v)     => ZIO.succeed(v)
              case Exit.Failure(cause) => ZIO.failCause(cause)
            }
            user <- resultZIO
            _ <- sendEmail(user.secret.get, user.id.getOrElse(0L), user.username, user.email).fork
          } yield Response.json(user.toJson)
      }
  }

  def updateUser(body: Body, id: Long): ZIO[Database, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) => {
          for {
            resultZIO <- for {
              fib <- userRepository
                .updateUser(user, id, new Date())
                .fork
              res <- fib.await
            } yield res match {
              case Exit.Success(v)     => ZIO.succeed(v)
              case Exit.Failure(cause) => ZIO.failCause(cause)
            }
            user <- resultZIO
          } yield Response.json(user.toJson)
        }
      }
  }
}
