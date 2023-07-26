package com.raunakjodhawat.nearby.utils

import courier._

import scala.util.{Failure, Properties, Success}
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.UUID.randomUUID
import javax.mail.internet.InternetAddress

import zio._
object Utils {
  val mailerHost = Properties.envOrElse("mailerHost", "")
  val mailerUsername = Properties.envOrElse("mailerUsername", "")
  val mailerPassword = Properties.envOrElse("mailerPassword", "")
  val env = Properties.envOrElse("env", "")

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

  def createUrl(secret: String): String = env match {
    case "development" => s"http://localhost:8080/api/v1/verify/$secret"
  }
  def sendEmail(secret: String, username: String, receiverEmail: String): ZIO[Any, Throwable, Unit] = {
    val url = createUrl(secret)
    val content =
      s"""
         |Hello $username,
         |
         |Please click $url to verify your email address.
         |If the above link does not work, please copy and paste the following $url in your browser.
         |
         |Thank you,
         |Nearby Team
         |""".stripMargin

    val mailerZIO: ZIO[Any, Throwable, Mailer] = ZIO.attempt(
      Mailer(mailerHost, 587)
        .auth(true)
        .as(mailerUsername, mailerPassword)
        .startTls(true)()
    )

    for {
      mailer <- mailerZIO
      mailSenderFiber <- ZIO.fromFuture { ex =>
        {
          mailer(
            Envelope
              .from(new InternetAddress(mailerUsername))
              .to(new InternetAddress(receiverEmail))
              .subject("Nearby, verify your email")
              .content(Text(content))
          )
        }
      }.fork
      mailSenderResult <- mailSenderFiber.await
    } yield mailSenderResult match {
      case Exit.Success(_) => println("message delivered")
      case Exit.Failure(_) => println(s"delivery failed")
    }
  }
}
