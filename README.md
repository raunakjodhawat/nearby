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
`sbt test` is containerized. Hence, it requires docker to be installed on your machine.
1. chmod +x ./scripts/runTests.sh
2. ./scripts/runTests.sh

## Endpoints
| Method | Endpoint       | Description |
| -- |----------------| --- |
| GET | api/v1/user    | Get all users |
| GET | api/v1/user/:id | Get a user by id |
| POST | api/v1/user    | Create a user |
| POST | api/v1/user/:id     | Update a user |


## Functionality
1. Sign up: Users can create a new account with a username and password. Username and Email address both needs to be unique. Upon successful user creation, an email is sent with a verification link. User needs to verify the email address before logging in.


## License
[MIT](https://choosealicense.com/licenses/mit/)

## Author
[Raunak Jodhawat](https://www.linkedin.com/in/jodhawat/)

## Contribution
Feel free to contribute to the project by creating a pull request.
I am looking for some React developers to help me with the frontend of the project.
