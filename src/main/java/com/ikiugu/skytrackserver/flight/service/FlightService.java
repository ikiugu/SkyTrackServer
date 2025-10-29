package com.ikiugu.skytrackserver.flight.service;

import com.ikiugu.skytrackserver.flight.Flight;
import com.ikiugu.skytrackserver.flight.FlightRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlightService {
  private final FlightRepository flightRepository;

  public FlightService(FlightRepository flightRepository) {
    this.flightRepository = flightRepository;
  }

  @Transactional
  public Flight upsertFlight(Flight flight) {
    Optional<Flight> existing =
        flightRepository.findByFlightNumberAndFlightDate(
            flight.getFlightNumber(), flight.getFlightDate());

    if (existing.isPresent()) {
      Flight existingFlight = existing.get();
      existingFlight.setDepTime(flight.getDepTime());
      existingFlight.setArrTime(flight.getArrTime());
      existingFlight.setGate(flight.getGate());
      existingFlight.setStatus(flight.getStatus());
      existingFlight.setDepIata(flight.getDepIata());
      existingFlight.setArrIata(flight.getArrIata());
      return flightRepository.save(existingFlight);
    } else {
      return flightRepository.save(flight);
    }
  }

  public List<Flight> findFlights(String depIata, String arrIata, LocalDate date) {
    return flightRepository.findByDepIataAndArrIataAndFlightDate(depIata, arrIata, date);
  }

  public Optional<Flight> findByFlightNumber(String flightNumber, LocalDate date) {
    return flightRepository.findByFlightNumberAndFlightDate(flightNumber, date);
  }

  public void triggerRefreshIfStale(String depIata, String arrIata, LocalDate date) {
    // Async refresh trigger - to be implemented with scheduler integration
    // For now, this is a placeholder for future async refresh logic
  }
}
