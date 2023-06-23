# Social Network nearby
## Description
Nearby is a Social Networking site built with of [ZIO](https://zio.dev) + [Scala](https://www.scala-lang.org) + [Postgres](https://www.postgresql.org) using [Slick](https://scala-slick.org)

It enables you to connect with people near you, get there services, see relevant ads based on location and much more.

## Installation
1. Clone the repository
2. Install [Postgres](https://www.postgresql.org) and create a database named `nearby` with a user named `nearby` with password `nearby`
3. Run postgres on your machine
4. Run `sbt run` in the root directory of the project
5. install insomnia or postman and test the [endpoints](./Insomnia.json)
6. Enjoy!

## Running test
1. create a database named `nearby_test` with a user named `nearby_test` with password `nearby_test`
2. Run `sbt test` in the root directory of the project
3. Run `sbt coverage +test +coverageReport` to generate coverage report
4. Run `sbt coverageAggregate` to generate aggregated coverage report

## Endpoints
| Method | Endpoint       | Description |
| -- |----------------| --- |
| GET | api/v1/user    | Get all users |
| GET | api/v1/user/:id | Get a user by id |
| POST | api/v1/user    | Create a user |
| POST | api/v1/user/:id     | Update a user |

## License
[MIT](https://choosealicense.com/licenses/mit/)

## Author
[Raunak Jodhawat](https://www.linkedin.com/in/jodhawat/)

## Contribution
Feel free to contribute to the project by creating a pull request.
I am looking for some React developers to help me with the frontend of the project.
