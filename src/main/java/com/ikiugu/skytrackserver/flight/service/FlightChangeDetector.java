package com.ikiugu.skytrackserver.flight.service;

import com.ikiugu.skytrackserver.flight.Flight;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FlightChangeDetector {

  public Optional<FlightChange> detectChange(Flight existing, Flight incoming) {
    boolean gateChanged = false;
    boolean statusChanged = false;
    boolean delaySignificant = false;
    boolean isUrgent = false;

    // Gate change detection
    if (existing.getGate() != null && !existing.getGate().equals(incoming.getGate())) {
      gateChanged = true;
    } else if (existing.getGate() == null && incoming.getGate() != null) {
      gateChanged = true;
    }

    // Status change detection
    if (!existing.getStatus().equals(incoming.getStatus())) {
      statusChanged = true;
    }

    // Delay calculation (using scheduled vs estimated times as proxy)
    long existingDelayMinutes = calculateDelayMinutes(existing);
    long incomingDelayMinutes = calculateDelayMinutes(incoming);
    if (incomingDelayMinutes - existingDelayMinutes > 15) {
      delaySignificant = true;
    }

    // Urgent criteria: delay > 30 minutes or cancelled
    if (incomingDelayMinutes > 30 || "cancelled".equalsIgnoreCase(incoming.getStatus())) {
      isUrgent = true;
    }

    // Return change if any detected
    if (gateChanged || statusChanged || delaySignificant || isUrgent) {
      return Optional.of(
          new FlightChange(
              incoming.getFlightNumber(),
              existing.getGate(),
              incoming.getGate(),
              existing.getStatus(),
              incoming.getStatus(),
              (int) incomingDelayMinutes,
              isUrgent));
    }

    return Optional.empty();
  }

  private long calculateDelayMinutes(Flight flight) {
    if (flight.getDepTime() == null) {
      return 0;
    }
    // For now, return 0 - actual delay would come from API delay field
    // This is a placeholder - in real implementation, we'd track delay from API
    return 0;
  }

  public record FlightChange(
      String flightNumber,
      String oldGate,
      String newGate,
      String oldStatus,
      String newStatus,
      Integer delayMinutes,
      boolean isUrgent) {}
}
