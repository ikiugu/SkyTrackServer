package com.ikiugu.skytrackserver.flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
  List<Flight> findByDepIataAndArrIataAndFlightDate(String depIata, String arrIata, LocalDate date);

  Optional<Flight> findByFlightNumberAndFlightDate(String flightNumber, LocalDate date);
}
