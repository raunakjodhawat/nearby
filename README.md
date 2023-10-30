# Social Network nearby
## Description
Nearby is a Social Networking site built with of [ZIO](https://zio.dev) + [Scala - 3.3.1](https://www.scala-lang.org) + [Postgres](https://www.postgresql.org) using [Slick](https://scala-slick.org)

It enables you to connect with people near you, get there services, see relevant ads based on location and much more.

## Installation
1. Clone the repository
2. Install [Postgres](https://www.postgresql.org) and create a database named `nearby` with a user named `nearby` with password `nearby`
3. Run postgres on your machine
4. Execute 
```shell
   sbt run
```
5. install insomnia or postman and test the [endpoints](./Insomnia.json)
6. Enjoy!

## Running test
### With DB on Docker 
1. chmod +x ./scripts/runTests.sh
2. ./scripts/runTests.sh

### On Local Test DB
1. Run postgres on your machine
2. Create `nearby_test` database with user `nearby_test` and password `nearby_test`
3. Run `sbt test` in the root directory of the project
4. It's always a good idea to have maximum code coverage. To check code coverage run `sbt coverage +test +coverageReport` in the root directory of the project.
5. Coverage report can be found at `target/scala-3.3.1/scoverage-report/index.html`

## Tech Stack
1. [ZIO](https://zio.dev) - A type-safe, composable library for async and concurrent programming in Scala
2. [Scala](https://www.scala-lang.org) - A general purpose programming language
3. [Postgres](https://www.postgresql.org) - A powerful, open source object-relational database system
4. [Slick](https://scala-slick.org) - A modern database query and access library for Scala
5. [ScalaTest](https://www.scalatest.org) - A testing tool for Scala and Java developers
6. [Docker](https://www.docker.com) - A set of platform as a service products that use OS-level virtualization to deliver software in packages called containers
7. [Insomnia](https://insomnia.rest) - A powerful REST API Client
8. [Cats](https://typelevel.org/cats/) - Lightweight, modular, and extensible library for functional programming
9. [Cats Effect](https://typelevel.org/cats-effect/) - The most popular purely functional effect library for Scala

## Endpoints
| Method | Endpoint            | Description              |
|--------|---------------------|--------------------------|
| GET    | api/v1/user/:id     | Get a user by id         |
| POST   | api/v1/user/:id     | Update a user            |
| POST   | api/v1/authenticate | Authenticate Auth header |
| POST   | api/v1/login        | Login a user             |
| GET    | api/v1/ping         | Health check URL         |


## Functionality
1. User can login. During login, if it's a new user we create the account with the given username and password.
2. All subsequent request needs auth token to validate themselves

## License
[MIT](https://choosealicense.com/licenses/mit/)

## Author
[Raunak Jodhawat](https://www.linkedin.com/in/jodhawat/)

## Contribution
Feel free to contribute to the project by creating a pull request.
I am looking for some React developers to help me with the [frontend](https://github.com/raunakjodhawat/nearby-frontend) of the project.
