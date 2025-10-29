package com.ikiugu.skytrackserver.flight.mapper;

import com.ikiugu.skytrackserver.aviation.dto.AviationstackResponse.FlightData;
import com.ikiugu.skytrackserver.flight.Flight;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {
  private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  public Flight toFlight(FlightData flightData, LocalDate flightDate) {
    Flight flight = new Flight();
    flight.setFlightNumber(flightData.flight().number());
    flight.setDepIata(flightData.departure().iata());
    flight.setArrIata(flightData.arrival().iata());
    flight.setDepTime(parseTimestamp(flightData.departure().scheduled()));
    flight.setArrTime(parseTimestamp(flightData.arrival().scheduled()));

    // Handle nullable gate - use estimated/actual if gate is null
    String gate = flightData.departure().gate();
    if (gate == null || gate.isBlank()) {
      // Try arrival gate as fallback
      gate = flightData.arrival().gate();
    }
    flight.setGate(gate);

    // Extract status from flight_status field
    String status = flightData.flightStatus();
    if (status == null || status.isBlank()) {
      status = "scheduled"; // default
    }
    flight.setStatus(status);

    flight.setFlightDate(flightDate);

    return flight;
  }

  private Instant parseTimestamp(String timestamp) {
    if (timestamp == null || timestamp.isBlank()) {
      throw new IllegalArgumentException("Timestamp cannot be null or blank");
    }
    try {
      // Handle ISO 8601 format with timezone
      return Instant.parse(timestamp);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid timestamp format: " + timestamp, e);
    }
  }
}
