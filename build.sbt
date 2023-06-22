ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "nearby"
  )
val circeVersion = "0.14.5"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.15",
  "dev.zio" %% "zio-http" % "3.0.0-RC2",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "com.typesafe.slick" %% "slick-testkit" % "3.4.1" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "ch.qos.logback" % "logback-classic" % "1.4.7" % "test",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1",
  "org.postgresql" % "postgresql" % "42.5.4",
  "dev.zio" %% "zio-test" % "2.0.15" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.0.15" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.15" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

parallelExecution in Test := false

logBuffered := false
