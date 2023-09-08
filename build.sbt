ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "nearby"
  )
val zioVersion = "2.0.16"
val zioHttpVersion = "3.0.0-RC2"
val slickVersion = "3.5.0-M4"
val circeVersion = "0.14.5"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % zioHttpVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "org.postgresql" % "postgresql" % "42.5.4",
  // email
  "com.github.daddykotex" %% "courier" % "3.2.0",
  // jwt token
  "com.github.jwt-scala" %% "jwt-zio-json" % "9.4.0",
  // zio test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-test-junit" % zioVersion % "test",
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalatestplus" %% "junit-4-13" % "3.2.15.0" % Test,
  "info.senia" %% "zio-test-akka-http" % "2.0.4",
  // json
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

coverageEnabled := false
// Coverage settings
coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 70
coverageMinimumBranchTotal := 70
coverageMinimumStmtPerPackage := 70
coverageMinimumBranchPerPackage := 70
coverageMinimumStmtPerFile := 70
coverageMinimumBranchPerFile := 70
