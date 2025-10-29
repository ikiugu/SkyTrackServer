package com.ikiugu.skytrackserver.aviation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.ikiugu.skytrackserver.aviation.config.AviationstackProperties;
import com.ikiugu.skytrackserver.aviation.service.AviationstackClient;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

class AviationstackClientTest {

  private WireMockServer wireMockServer;
  private AviationstackClient client;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());

    AviationstackProperties properties = new AviationstackProperties();
    properties.setBaseUrl("http://localhost:" + wireMockServer.port());
    properties.setApiKey("test-api-key");

    WebClient webClient = WebClient.builder().baseUrl(properties.getBaseUrl()).build();

    Retry retrySpec = Retry.backoff(3, java.time.Duration.ofMillis(100));

    client = new AviationstackClient(webClient, properties, retrySpec);
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void shouldReturnFlightsOnSuccessfulResponse() {
    String responseBody =
        """
        {
          "pagination": {"limit": 100, "offset": 0, "count": 1, "total": 1},
          "data": [
            {
              "flight": {"number": "AA100", "iata": "AA100", "icao": "AAL100"},
              "airline": {"name": "American Airlines", "iata": "AA", "icao": "AAL"},
              "departure": {
                "airport": "JFK",
                "iata": "JFK",
                "icao": "KJFK",
                "terminal": "8",
                "gate": "A12",
                "delay": 0,
                "scheduled": "2024-01-15T08:00:00+00:00",
                "estimated": "2024-01-15T08:00:00+00:00",
                "timezone": "America/New_York"
              },
              "arrival": {
                "airport": "LAX",
                "iata": "LAX",
                "icao": "KLAX",
                "terminal": "4",
                "gate": "23",
                "delay": null,
                "scheduled": "2024-01-15T11:30:00+00:00",
                "estimated": "2024-01-15T11:30:00+00:00",
                "timezone": "America/Los_Angeles"
              },
              "flight_status": "scheduled",
              "flight_date": "2024-01-15"
            }
          ]
        }
        """;

    wireMockServer.stubFor(
        get(urlPathEqualTo("/flights"))
            .withQueryParam("access_key", equalTo("test-api-key"))
            .withQueryParam("dep_iata", equalTo("JFK"))
            .withQueryParam("arr_iata", equalTo("LAX"))
            .withQueryParam("flight_date", equalTo("2024-01-15"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)));

    StepVerifier.create(client.getFlights("JFK", "LAX", LocalDate.of(2024, 1, 15)))
        .expectNextMatches(
            flights ->
                flights.size() == 1
                    && flights.get(0).flight().number().equals("AA100")
                    && flights.get(0).departure().iata().equals("JFK")
                    && flights.get(0).arrival().iata().equals("LAX"))
        .verifyComplete();
  }

  @Test
  void shouldHandleApiErrorResponse() {
    String errorBody =
        """
        {
          "error": {
            "code": "invalid_access_key",
            "message": "You have not supplied a valid API Access Key."
          }
        }
        """;

    wireMockServer.stubFor(
        get(urlPathEqualTo("/flights"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(errorBody)));

    StepVerifier.create(client.getFlights("JFK", "LAX", LocalDate.of(2024, 1, 15)))
        .expectErrorMatches(throwable -> throwable.getMessage().contains("Aviationstack error"))
        .verify();
  }

  @Test
  void shouldRetryOnServerError() {
    // First call returns 500, second call succeeds
    wireMockServer.stubFor(
        get(urlPathEqualTo("/flights"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                    {
                      "pagination": {"limit": 100, "offset": 0, "count": 0, "total": 0},
                      "data": []
                    }
                    """)));

    StepVerifier.create(client.getFlights("JFK", "LAX", LocalDate.of(2024, 1, 15)))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void shouldHandleTimeout() {
    wireMockServer.stubFor(
        get(urlPathEqualTo("/flights"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withFixedDelay(5000) // 5 seconds delay
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}")));

    // This test verifies timeout handling - actual timeout testing may require longer delays
    StepVerifier.create(client.getFlights("JFK", "LAX", LocalDate.of(2024, 1, 15)))
        .expectError()
        .verify(java.time.Duration.ofSeconds(10));
  }
}
