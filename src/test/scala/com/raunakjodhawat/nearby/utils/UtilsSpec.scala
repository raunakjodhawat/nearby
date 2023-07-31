package com.raunakjodhawat.nearby.utils

import com.raunakjodhawat.nearby.utils.Utils._
import courier._
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import zio._
import zio.test._
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

@RunWith(classOf[ZTestJUnitRunner])
class UtilsSpec extends JUnitRunnableSpec with MockFactory {
  def spec = suite("Utils Spec")(
    suite("constants")(
      test("should load and remain unchanged") {
        zio.test.assert(mailerHost)(Assertion.equalTo("smtp.test-email-sender.org")) &&
        zio.test.assert(mailerUsername)(Assertion.equalTo("raunakjodhawat@gmailcom")) &&
        zio.test.assert(mailerPassword)(Assertion.equalTo("some-random-test-password")) &&
        zio.test.assert(env)(Assertion.equalTo("test"))
      },
      test("validated creation of email body") {
        zio.test.assert(createEmailContent("raunakjodhawat", "some-url"))(
          Assertion.containsString("raunakjodhawat") && Assertion.containsString("Nearby Team") && Assertion
            .containsString("some-url")
        )
      }
    ),
    suite("validators")(
      suite("email validator")(
        test("verify randomly generated strings") {
          val gen = Gen.stringBounded(3, 10)(Gen.alphaChar)
          val emailAddressesGenerator = gen.zip(gen).map(x => s"${x._1}@${x._2}.com")
          check(emailAddressesGenerator) { email =>
            assertTrue(isValidEmail(email))
          }
        },
        test("failure in case when input is empty") {
          assertTrue(!isValidEmail(""))
        },
        test("failure in case when email.is not valid") {
          assertTrue(!isValidEmail("raunakjodhawat"))
        }
      ),
      suite("createUrl Validator")(
        zio.test.test("url creation") {
          val idGenerator = Gen.long
          val secretGenerator = Gen.string
          check(idGenerator.zip(secretGenerator)) { gen =>
            {
              val (id, secret) = gen
              zio.test.assert(createUrl(id, secret))(
                Assertion.equalTo(s"http://localhost:8080/api/v1/verify/$id/$secret")
              )
            }
          }

        }
      ),
      suite("username validator")(
        test("valid usernames") {
          val gen = Gen.stringBounded(6, 31)(Gen.alphaChar)
          check(gen) { username =>
            assertTrue(isValidUsername(username))
          }
        },
        test("invalid usernames") {
          assertTrue(!isValidUsername("12345")) && assertTrue(!isValidUsername("hakuna-matata-matata-hakuna-matata"))
        }
      ),
      suite("Password validator")(
        test("valid passwords") {
          val gen = Gen.stringBounded(6, 31)(Gen.alphaChar)
          check(gen) { username =>
            assertTrue(isValidPassword(username))
          }
        },
        test("invalid passwords") {
          assertTrue(!isValidPassword("12345")) && assertTrue(!isValidPassword("hakuna-matata-matata-hakuna-matata"))
        }
      )
    ),
    suite("email sender functionality")(
      test("should be able to send an email") {
        val mockMailer = mock[Mailer]
        val mockZIO: ZIO[Any, Throwable, Mailer] = ZIO.succeed(mockMailer)
        assertZIO(sendEmail("secret", 1L, "raunak", "raunakjodhawat@gmail.com", mockZIO))(Assertion.isUnit)
      }
    )
  )
}
