FROM sbtscala/scala-sbt:graalvm-ce-22.3.0-b2-java17_1.8.2_2.13.10

WORKDIR /nearBy
COPY . /nearBy
ADD . /nearBy
CMD sbt run
