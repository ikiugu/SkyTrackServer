# SkyTrack Server

Backend server application for SkyTrack built with Spring Boot.

## Technologies

- **Java 17**
- **Spring Boot 3.5.7**
- **GraphQL** - API layer
- **Spring Security** - Authentication and authorization
- **Apache Kafka** - Event streaming
- **Spring Actuator** - Application monitoring

## Building the Project

This project uses Gradle. To build the project:

```bash
./gradlew build
```

## Running the Application

To run the application:

```bash
./gradlew bootRun
```

Or if you have built the JAR:

```bash
java -jar build/libs/skytrackserver-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/com/ikiugu/skytrackserver/
│   │   └── SkyTrackServerApplication.java
│   └── resources/
│       ├── application.properties
│       └── graphql/
└── test/
    └── java/com/ikiugu/skytrackserver/
        └── SkyTrackServerApplicationTests.java
```

## Development

The application will start on the default Spring Boot port (8080) unless configured otherwise in `application.properties`.

