# Introduction

This is an example project for Game Tracker

# Getting Started

To import the project to your IDE and run it locally follow this section.

## Dependencies

This project contains following modules:

1. player-service
2. game-service
3. commons

Import them all together in IntelliJ IDEA or on another IDE.

## Build and run

Run `mvn clean install`

Run both 

* PlayerApplication (com/numarics/player/PlayerApplication.java)
* GameApplication (com/numarics/game/GameApplication.java)

Application will expose endpoints under:

player-service: [http://localhost:8081](http://localhost:8081)
game-service: [http://localhost:8082](http://localhost:8082)

## Endpoints

* H2 Console - player: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
* H2 Console - game: [http://localhost:8082/h2-console](http://localhost:8082/h2-console)

## Postman

In order to test Microservice endpoints you can use Postman https://www.postman.com/
Import the collection and environment to Postman app from folder 'collection'

Have fun! 
