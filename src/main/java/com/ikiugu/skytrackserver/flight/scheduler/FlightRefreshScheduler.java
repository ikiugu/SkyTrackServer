package com.ikiugu.skytrackserver.flight.scheduler;

import com.ikiugu.skytrackserver.aviation.dto.AviationstackResponse.FlightData;
import com.ikiugu.skytrackserver.aviation.service.AviationstackClient;
import com.ikiugu.skytrackserver.events.FlightStatusEvent;
import com.ikiugu.skytrackserver.events.producer.FlightStatusEventProducer;
import com.ikiugu.skytrackserver.flight.Flight;
import com.ikiugu.skytrackserver.flight.mapper.FlightMapper;
import com.ikiugu.skytrackserver.flight.service.FlightChangeDetector;
import com.ikiugu.skytrackserver.flight.service.FlightService;
import com.ikiugu.skytrackserver.flight.service.StatusVersionService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FlightRefreshScheduler {
  private static final Logger logger = LoggerFactory.getLogger(FlightRefreshScheduler.class);

  private final AviationstackClient aviationstackClient;
  private final FlightService flightService;
  private final FlightMapper flightMapper;
  private final FlightChangeDetector changeDetector;
  private final FlightStatusEventProducer eventProducer;
  private final StatusVersionService statusVersionService;

  public FlightRefreshScheduler(
      AviationstackClient aviationstackClient,
      FlightService flightService,
      FlightMapper flightMapper,
      FlightChangeDetector changeDetector,
      FlightStatusEventProducer eventProducer,
      StatusVersionService statusVersionService) {
    this.aviationstackClient = aviationstackClient;
    this.flightService = flightService;
    this.flightMapper = flightMapper;
    this.changeDetector = changeDetector;
    this.eventProducer = eventProducer;
    this.statusVersionService = statusVersionService;
  }

  @Scheduled(fixedRateString = "${flight.refresh.interval:7200000}") // 2 hours default
  public void refreshPopularFlights() {
    LocalDate today = LocalDate.now();
    logger.info("Starting scheduled flight refresh for date: {}", today);

    // Configurable routes - for now using common routes
    String[][] routes = {
      {"JFK", "LAX"},
      {"JFK", "SFO"},
      {"LHR", "CDG"},
      {"LAX", "JFK"}
    };

    for (String[] route : routes) {
      String depIata = route[0];
      String arrIata = route[1];
      refreshRoute(depIata, arrIata, today);
    }

    logger.info("Completed scheduled flight refresh");
  }

  private void refreshRoute(String depIata, String arrIata, LocalDate date) {
    try {
      logger.debug("Refreshing flights for route: {} -> {} on {}", depIata, arrIata, date);
      List<FlightData> flightDataList =
          aviationstackClient.getFlights(depIata, arrIata, date).block();

      if (flightDataList == null || flightDataList.isEmpty()) {
        logger.debug("No flights found for route: {} -> {} on {}", depIata, arrIata, date);
        return;
      }

      int updated = 0;
      AtomicInteger changed = new AtomicInteger(0);

      for (FlightData flightData : flightDataList) {
        try {
          Flight incomingFlight = flightMapper.toFlight(flightData, date);
          Flight existingFlight =
              flightService.findByFlightNumber(incomingFlight.getFlightNumber(), date).orElse(null);

          Flight savedFlight = flightService.upsertFlight(incomingFlight);
          updated++;

          // Detect changes if existing flight was present
          if (existingFlight != null) {
            changeDetector
                .detectChange(existingFlight, savedFlight)
                .ifPresent(
                    change -> {
                      logger.info(
                          "Flight change detected for {}: {} -> {}",
                          change.flightNumber(),
                          change.oldStatus(),
                          change.newStatus());
                      changed.incrementAndGet();

                      // Increment version and publish event
                      Long version = statusVersionService.incrementVersion(change.flightNumber());
                      FlightStatusEvent event =
                          new FlightStatusEvent(
                              change.flightNumber(),
                              change.newStatus(),
                              change.newGate(),
                              change.delayMinutes(),
                              version,
                              Instant.now(),
                              change.isUrgent());
                      eventProducer.publishFlightStatusChange(event);
                    });
          }
        } catch (Exception e) {
          logger.error("Error processing flight: {}", flightData.flight().number(), e);
        }
      }

      logger.info(
          "Route {} -> {}: Updated {} flights, detected {} changes",
          depIata,
          arrIata,
          updated,
          changed.get());
    } catch (Exception e) {
      logger.error("Error refreshing route {} -> {} on {}", depIata, arrIata, date, e);
    }
  }
}
