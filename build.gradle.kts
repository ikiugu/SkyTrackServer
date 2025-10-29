plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "6.23.0"
}

group = "com.ikiugu"
version = "0.0.1-SNAPSHOT"
description = "Backend project for skystrack "

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.kafka:spring-kafka")
	
	// Database
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	
	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
	
	// Google OAuth
	implementation("com.google.api-client:google-api-client:2.2.0")
	
	// Firebase
	implementation("com.google.firebase:firebase-admin:9.2.0")
	
	// Rate Limiting
	implementation("com.bucket4j:bucket4j-core:8.7.0")
	
	// Mapping (optional but recommended)
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	
	// Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")
	
	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.wiremock:wiremock-standalone:3.4.0")
	testImplementation("org.skyscreamer:jsonassert:1.5.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Spotless configuration
spotless {
	java {
		target("src/**/*.java")
		googleJavaFormat("1.22.0")
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
	}
}

// Docker Compose tasks
tasks.register("composeUp") {
	doLast {
		project.exec {
			commandLine("docker", "compose", "-f", "docker/docker-compose.yml", "up", "-d")
		}
	}
}

tasks.register("composeDown") {
	doLast {
		project.exec {
			commandLine("docker", "compose", "-f", "docker/docker-compose.yml", "down", "-v")
		}
	}
}
