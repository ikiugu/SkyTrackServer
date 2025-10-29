package com.ikiugu.skytrackserver.aviation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AviationstackResponse(Pagination pagination, List<FlightData> data, ErrorInfo error) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Pagination(Integer limit, Integer offset, Integer count, Integer total) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ErrorInfo(String code, String message) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FlightData(
      FlightInfo flight,
      DepartureInfo departure,
      ArrivalInfo arrival,
      AirlineInfo airline,
      @JsonProperty("flight_status") String flightStatus,
      @JsonProperty("flight_date") String flightDate) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FlightInfo(String number, String iata, String icao) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record AirlineInfo(String name, String iata, String icao) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record DepartureInfo(
      String airport,
      String iata,
      String icao,
      String terminal,
      String gate,
      Integer delay,
      String scheduled,
      String estimated,
      String actual,
      String timezone) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record ArrivalInfo(
      String airport,
      String iata,
      String icao,
      String terminal,
      String gate,
      String baggage,
      Integer delay,
      String scheduled,
      String estimated,
      String actual,
      String timezone) {}
}
