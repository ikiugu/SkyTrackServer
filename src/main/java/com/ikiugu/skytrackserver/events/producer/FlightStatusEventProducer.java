package com.ikiugu.skytrackserver.events.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikiugu.skytrackserver.events.FlightStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlightStatusEventProducer {
  private static final Logger logger = LoggerFactory.getLogger(FlightStatusEventProducer.class);
  private static final String TOPIC = "flight-status";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public FlightStatusEventProducer(
      KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void publishFlightStatusChange(FlightStatusEvent event) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(TOPIC, event.flightNumber(), jsonPayload);
      logger.info(
          "Published flight status event for {} with version {}",
          event.flightNumber(),
          event.version());
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize flight status event: {}", event, e);
      throw new RuntimeException("Failed to publish flight status event", e);
    } catch (Exception e) {
      logger.error("Failed to publish flight status event: {}", event, e);
      throw new RuntimeException("Failed to publish flight status event", e);
    }
  }
}
