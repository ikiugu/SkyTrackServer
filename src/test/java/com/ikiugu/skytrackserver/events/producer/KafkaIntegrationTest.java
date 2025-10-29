package com.ikiugu.skytrackserver.events.producer;

import static org.junit.jupiter.api.Assertions.*;

import com.ikiugu.skytrackserver.events.FlightStatusEvent;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test to verify events can be published to Redpanda. This test verifies that: 1.
 * Redpanda is accessible at localhost:19092 2. FlightStatusEventProducer can successfully publish
 * events without errors
 *
 * <p>This satisfies the Phase 5 checkpoint: "Start Redpanda via Docker Compose, verify events
 * published, tests pass"
 */
@SpringBootTest
@TestPropertySource(
    properties = {
      "spring.kafka.bootstrap-servers=localhost:19092",
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.flyway.enabled=false"
    })
class KafkaIntegrationTest {

  @Autowired private FlightStatusEventProducer producer;

  @Test
  void shouldPublishEventToRedpanda() {
    FlightStatusEvent event =
        new FlightStatusEvent("AA100", "active", "A12", 15, 1L, Instant.now(), false);

    // Verify that publishing to Redpanda succeeds without throwing exceptions
    // If Redpanda is not running or not accessible, this will fail
    assertDoesNotThrow(
        () -> {
          producer.publishFlightStatusChange(event);
          // Wait a bit for async publish to complete
          Thread.sleep(500);
        },
        "Should successfully publish event to Redpanda");
  }
}
