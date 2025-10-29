package com.ikiugu.skytrackserver.events.producer;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikiugu.skytrackserver.events.FlightStatusEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration test to verify events can be published to Kafka-compatible broker in CI and locally.
 * This uses Testcontainers Kafka to avoid depending on a locally running Redpanda.
 */
@Testcontainers
@SpringBootTest
class KafkaIntegrationTest {

	private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:7.5.1");

	@Container
	static final KafkaContainer KAFKA = new KafkaContainer(KAFKA_IMAGE);

	@DynamicPropertySource
	static void registerKafkaProps(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
		registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
		registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.flyway.enabled", () -> "false");
	}

	@Autowired private FlightStatusEventProducer producer;
	@Autowired private ObjectMapper objectMapper;

	@Test
	void shouldPublishAndConsumeEventWithTestcontainersKafka() throws Exception {
		String topic = "flight-status";

		// Ensure topic exists (1 partition, replication factor 1)
		try (Admin admin = Admin.create(Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers()))) {
			admin.createTopics(Collections.singleton(new NewTopic(topic, 1, (short) 1))).all().get();
		}

		FlightStatusEvent event = new FlightStatusEvent("AA100", "active", "A12", 15, 1L, Instant.now(), false);

		// Publish - should not throw
		assertDoesNotThrow(() -> producer.publishFlightStatusChange(event));

		// Consume and verify
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singleton(topic));
			FlightStatusEvent received = null;
			long deadline = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
			while (received == null && System.currentTimeMillis() < deadline) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
				for (ConsumerRecord<String, String> record : records) {
					received = objectMapper.readValue(record.value(), FlightStatusEvent.class);
					break;
				}
			}

			assertNotNull(received, "Event should be consumed from Kafka");
			assertEquals("AA100", received.flightNumber());
			assertEquals("active", received.status());
			assertEquals(1L, received.version());
		}
	}
}
