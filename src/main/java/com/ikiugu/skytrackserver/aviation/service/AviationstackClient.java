package com.ikiugu.skytrackserver.aviation.service;

import com.ikiugu.skytrackserver.aviation.config.AviationstackProperties;
import com.ikiugu.skytrackserver.aviation.dto.AviationstackResponse;
import com.ikiugu.skytrackserver.aviation.dto.AviationstackResponse.FlightData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class AviationstackClient {
  private final WebClient webClient;
  private final AviationstackProperties properties;
  private final Retry retrySpec;

  public AviationstackClient(
      WebClient aviationstackWebClient,
      AviationstackProperties properties,
      Retry aviationstackRetrySpec) {
    this.webClient = aviationstackWebClient;
    this.properties = properties;
    this.retrySpec = aviationstackRetrySpec;
  }

  public Mono<List<FlightData>> getFlights(String depIata, String arrIata, LocalDate date) {
    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/flights")
                    .queryParam("access_key", properties.getApiKey())
                    .queryParam("dep_iata", depIata)
                    .queryParam("arr_iata", arrIata)
                    .queryParam("flight_date", date.toString())
                    .build())
        .retrieve()
        .bodyToMono(AviationstackResponse.class)
        .flatMap(
            response -> {
              if (response.error() != null) {
                return Mono.error(
                    new RuntimeException("Aviationstack error: " + response.error().message()));
              }
              return Mono.justOrEmpty(response.data());
            })
        .retryWhen(retrySpec);
  }
}
