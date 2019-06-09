# Demo ngChess 

Spring boot backend project for a chess web application.

The master of this repository is currently deployed on Heroku and you can access the whole app in https://demochess.herokuapp.com (you can use preloaded accounts demo/demo or demo2/demo2). _**Note:** The first load could take a couple of minutes (since the server could be sleeping)_.

### Tech

This project uses several technologies on top of Spring Boot, the most important are:

* [Spring websockets]
* [Spring security]
* [Spring JPA]
* [AspectJ] 
* [H2]

### Run the project

Requires Java 11 and Maven 3.x to run.

Straight with Maven:

_If you are using Windows you should edit `src/main/resources/application.properties` and uncomment the marked Stockfish config lines_

```sh
$ mvn clean spring-boot:run -Dspring-boot.run.arguments="initDb"
```

 

Build and run:

```sh
$ mvn clean package
$ java -jar target\demo.minichess-0.0.1-SNAPSHOT.jar initDb
```

