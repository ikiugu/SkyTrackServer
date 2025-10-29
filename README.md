# SkyTrack Server

Backend server application for SkyTrack built with Spring Boot.

## Related Repositories

- **[SkyTrack (Main)](https://github.com/ikiugu/SkyTrack)** - Complete project overview and architecture
- **[SkyTrack Android](https://github.com/ikiugu/SkyTrackAndroid)** - Android mobile application

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

## Environment Variables

Required for local development:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/skytrack
export DATABASE_USERNAME=sky
export DATABASE_PASSWORD=track
export AVIATIONSTACK_API_KEY=your_api_key_here
export AVIATIONSTACK_BASE_URL=https://api.aviationstack.com/v1
export OPENAI_API_KEY=sk-...
export GOOGLE_OAUTH_WEB_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
export JWT_SECRET=change-this-to-a-long-random-secret-key-minimum-256-bits
export FIREBASE_CONFIG_PATH=src/main/resources/firebase-service-account.json
export KAFKA_BOOTSTRAP_SERVERS=localhost:19092
```

For production, all secrets should be injected via secure secret management.

## Development

The application will start on the default Spring Boot port (8080) unless configured otherwise in `application.properties`.

### Docker Compose

To start the local development infrastructure (PostgreSQL and Redpanda):

```bash
./gradlew composeUp
```

To stop:

```bash
./gradlew composeDown
```

Or use docker-compose directly:

```bash
docker-compose -f docker/docker-compose.yml up -d
```
