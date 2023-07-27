ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "nearby"
  )
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.15",
  "dev.zio" %% "zio-http" % "3.0.0-RC2",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1",
  "org.postgresql" % "postgresql" % "42.5.4",
  // email
  "com.github.daddykotex" %% "courier" % "3.2.0",
  // jwt token
  "com.github.jwt-scala" %% "jwt-zio-json" % "9.4.0",
  // Scala test
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test,
  // zio test
  "dev.zio" %% "zio-test" % "2.0.15" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.0.15" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.15" % Test,
  "dev.zio" %% "zio-test-junit" % "2.0.15" % "test",
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

//coverageEnabled := true
// Coverage settings
coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 70
coverageMinimumBranchTotal := 70
coverageMinimumStmtPerPackage := 70
coverageMinimumBranchPerPackage := 70
coverageMinimumStmtPerFile := 70
coverageMinimumBranchPerFile := 70
Test / parallelExecution := false
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
