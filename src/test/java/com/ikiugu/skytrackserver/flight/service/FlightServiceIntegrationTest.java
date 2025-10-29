package com.ikiugu.skytrackserver.flight.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ikiugu.skytrackserver.flight.Flight;
import com.ikiugu.skytrackserver.flight.FlightRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FlightServiceIntegrationTest {

  @Autowired private FlightRepository flightRepository;

  private FlightService flightService;

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    flightService = new FlightService(flightRepository);
  }

  @Test
  void shouldCreateNewFlight() {
    Flight flight = createTestFlight("AA100", "A12", "scheduled");

    Flight saved = flightService.upsertFlight(flight);

    assertNotNull(saved.getId());
    assertEquals("AA100", saved.getFlightNumber());
    assertEquals("A12", saved.getGate());
    assertEquals(1, flightRepository.count());
  }

  @Test
  void shouldUpdateExistingFlight() {
    Flight existing = createTestFlight("AA100", "A12", "scheduled");
    existing = flightRepository.save(existing);

    Flight updated = createTestFlight("AA100", "B15", "active");
    updated.setFlightDate(existing.getFlightDate());
    Flight saved = flightService.upsertFlight(updated);

    assertEquals(existing.getId(), saved.getId());
    assertEquals("B15", saved.getGate());
    assertEquals("active", saved.getStatus());
    assertEquals(1, flightRepository.count());
  }

  @Test
  void shouldFindFlightsByRoute() {
    Flight flight1 = createTestFlight("AA100", "A12", "scheduled");
    flight1.setDepIata("JFK");
    flight1.setArrIata("LAX");
    flight1.setFlightDate(LocalDate.of(2024, 1, 15));
    flightRepository.save(flight1);

    Flight flight2 = createTestFlight("UA200", "B10", "scheduled");
    flight2.setDepIata("JFK");
    flight2.setArrIata("LAX");
    flight2.setFlightDate(LocalDate.of(2024, 1, 15));
    flightRepository.save(flight2);

    Flight flight3 = createTestFlight("DL300", "C5", "scheduled");
    flight3.setDepIata("JFK");
    flight3.setArrIata("SFO");
    flight3.setFlightDate(LocalDate.of(2024, 1, 15));
    flightRepository.save(flight3);

    List<Flight> flights = flightService.findFlights("JFK", "LAX", LocalDate.of(2024, 1, 15));

    assertEquals(2, flights.size());
    assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("AA100")));
    assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("UA200")));
  }

  @Test
  void shouldFindFlightByFlightNumber() {
    Flight flight = createTestFlight("AA100", "A12", "scheduled");
    flight.setFlightDate(LocalDate.of(2024, 1, 15));
    flightRepository.save(flight);

    Optional<Flight> found = flightService.findByFlightNumber("AA100", LocalDate.of(2024, 1, 15));

    assertTrue(found.isPresent());
    assertEquals("AA100", found.get().getFlightNumber());
  }

  @Test
  void shouldReturnEmptyWhenFlightNotFound() {
    Optional<Flight> found = flightService.findByFlightNumber("AA999", LocalDate.of(2024, 1, 15));

    assertFalse(found.isPresent());
  }

  private Flight createTestFlight(String flightNumber, String gate, String status) {
    Flight flight = new Flight();
    flight.setFlightNumber(flightNumber);
    flight.setDepIata("JFK");
    flight.setArrIata("LAX");
    flight.setDepTime(Instant.now());
    flight.setArrTime(Instant.now().plusSeconds(10800));
    flight.setGate(gate);
    flight.setStatus(status);
    flight.setFlightDate(LocalDate.now());
    return flight;
  }
}
