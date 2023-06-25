FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /app
COPY project .
COPY build.sbt .
COPY target/scala-2.13/classes .
EXPOSE 8000
CMD sbt run
