package com.ikiugu.skytrackserver.flight.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ikiugu.skytrackserver.flight.Flight;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlightChangeDetectorTest {

  private FlightChangeDetector detector;

  @BeforeEach
  void setUp() {
    detector = new FlightChangeDetector();
  }

  @Test
  void shouldDetectGateChange() {
    Flight existing = createFlight("AA100", "A12", "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "B15", "scheduled", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertTrue(change.isPresent());
    assertEquals("AA100", change.get().flightNumber());
    assertEquals("A12", change.get().oldGate());
    assertEquals("B15", change.get().newGate());
  }

  @Test
  void shouldDetectGateChangeFromNull() {
    Flight existing = createFlight("AA100", null, "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "A12", "scheduled", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertTrue(change.isPresent());
    assertNull(change.get().oldGate());
    assertEquals("A12", change.get().newGate());
  }

  @Test
  void shouldDetectStatusChange() {
    Flight existing = createFlight("AA100", "A12", "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "A12", "active", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertTrue(change.isPresent());
    assertEquals("scheduled", change.get().oldStatus());
    assertEquals("active", change.get().newStatus());
  }

  @Test
  void shouldDetectCancelledAsUrgent() {
    Flight existing = createFlight("AA100", "A12", "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "A12", "cancelled", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertTrue(change.isPresent());
    assertTrue(change.get().isUrgent());
    assertEquals("cancelled", change.get().newStatus());
  }

  @Test
  void shouldNotDetectChangeWhenNoChanges() {
    Flight existing = createFlight("AA100", "A12", "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "A12", "scheduled", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertFalse(change.isPresent());
  }

  @Test
  void shouldDetectMultipleChanges() {
    Flight existing = createFlight("AA100", "A12", "scheduled", Instant.now());
    Flight incoming = createFlight("AA100", "B15", "active", Instant.now());

    Optional<FlightChangeDetector.FlightChange> change = detector.detectChange(existing, incoming);

    assertTrue(change.isPresent());
    assertEquals("A12", change.get().oldGate());
    assertEquals("B15", change.get().newGate());
    assertEquals("scheduled", change.get().oldStatus());
    assertEquals("active", change.get().newStatus());
  }

  private Flight createFlight(String flightNumber, String gate, String status, Instant depTime) {
    Flight flight = new Flight();
    flight.setFlightNumber(flightNumber);
    flight.setGate(gate);
    flight.setStatus(status);
    flight.setDepTime(depTime);
    flight.setArrTime(depTime.plusSeconds(10800)); // 3 hours later
    flight.setFlightDate(LocalDate.now());
    flight.setDepIata("JFK");
    flight.setArrIata("LAX");
    return flight;
  }
}
