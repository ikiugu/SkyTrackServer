package com.ikiugu.skytrackserver.events.producer;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikiugu.skytrackserver.events.FlightStatusEvent;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
    properties = {
      "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.flyway.enabled=false"
    })
@EmbeddedKafka(
    partitions = 1,
    topics = {"flight-status"},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@DirtiesContext
class FlightStatusEventProducerTest {

  @Autowired private FlightStatusEventProducer producer;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldPublishFlightStatusEvent() {
    FlightStatusEvent event =
        new FlightStatusEvent("AA100", "active", "A12", 15, 1L, Instant.now(), false);

    // Should not throw exception
    assertDoesNotThrow(() -> producer.publishFlightStatusChange(event));
  }

  @Test
  void shouldSerializeEventCorrectly() throws Exception {
    FlightStatusEvent event =
        new FlightStatusEvent("AA100", "active", "A12", 15, 1L, Instant.now(), false);

    String json = objectMapper.writeValueAsString(event);
    assertNotNull(json);
    assertTrue(json.contains("AA100"));
    assertTrue(json.contains("active"));

    FlightStatusEvent deserialized = objectMapper.readValue(json, FlightStatusEvent.class);
    assertEquals("AA100", deserialized.flightNumber());
    assertEquals("active", deserialized.status());
    assertEquals("A12", deserialized.newGate());
    assertEquals(15, deserialized.delayMinutes());
    assertEquals(1L, deserialized.version());
  }
}
