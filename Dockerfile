FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /app
RUN sh echo "hello world"
COPY . .
COPY build.sbt .
EXPOSE 8080
CMD sbt coverage +test +coverageReport