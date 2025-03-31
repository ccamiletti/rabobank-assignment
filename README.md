
# Rabo Assignment-App

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) sample app.

## Requirements

For building and running the application you need:

- [JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3](https://maven.apache.org)
- [Docker](https://www.docker.com/)
## Running the application locally

This app is running in a docker container

```shell
docker compose up -d
```

This will create:

* A container with:
    - Rabobank-Assgnment-App

* A container with:
    - [PostgresSQL Adminer](http://localhost:1010/?pgsql=ccs-tech-server&username=ccs-tech-user&db=ccs-tech-db&ns=public)

* A container with:
    - PostgresSQL Database

## About the Service

The service is just a simple bank account app. It uses database to store the data (Postgres).
The application runs in a fully asynchronous environment taking advantage of webflux and r2dbc for database connections.

By default, the database is populated with the following data:
* User Information
    * User1:
        * username: test@gmail.com
        * password: test
    * User2:
        * username: test2@gmail.com
        * password: test2

* Card Information
    * Card1:
        * number: 11111
        * iban: RABO111
        * type: DEBIT_CARD
    * Card2:
        * number: 22222
        * iban: RABO222
        * type: CREDIT_CARD

Here are some endpoints you can call:


### Swagger api documentation

* http://localhost:8080/api/swagger-ui/index.html


### Login (No Auth)

```
GET http://localhost:8080/api/login?userName=test@gmail.com&password=test
Accept: application/json

RESPONSE: HTTP 200
{
    "userName":"test",
    "token":"token-string"
} 
```

### Transfer (Bearer Token)
```
POST http://localhost:8080/api/account/transfer
Accept: application/json
Content-Type: application/json
{
    "originAccountNumber": "RABO111",
    "targetAccountNumber": "RABO222",
    "amount": 100
}

RESPONSE: HTTP 200
```

### Withdrawal (Bearer Token)
```
POST http://localhost:8080/api/account/withdraw
Accept: application/json
Content-Type: application/json
{
    "cardNumber": "11111",
    "amount": 100
}

RESPONSE: HTTP 200
```

### All accounts (Bearer Token)
```
GET http://localhost:8080/api/account/all

BODY RESPONSE:
[
    {
        "accountNumber": "RABO111",
        "balance": 20000.0
    },
    {
        "accountNumber": "RABO222",
        "balance": 20000.0
    }
]
```