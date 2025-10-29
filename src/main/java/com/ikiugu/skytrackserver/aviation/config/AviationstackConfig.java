package com.ikiugu.skytrackserver.aviation.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

@Configuration
public class AviationstackConfig {

  @Bean
  public WebClient aviationstackWebClient(AviationstackProperties props) {
    HttpClient httpClient =
        HttpClient.create().responseTimeout(Duration.ofSeconds(30)).compress(true);

    ExchangeFilterFunction errorFilter =
        ExchangeFilterFunction.ofResponseProcessor(
            clientResponse -> {
              if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse
                    .createException()
                    .flatMap(e -> reactor.core.publisher.Mono.error(e));
              }
              return reactor.core.publisher.Mono.just(clientResponse);
            });

    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .clientConnector(
            new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
        .filter(errorFilter)
        .build();
  }

  @Bean
  public Retry aviationstackRetrySpec() {
    return Retry.backoff(3, Duration.ofMillis(300))
        .filter(throwable -> true)
        .onRetryExhaustedThrow((spec, signal) -> signal.failure());
  }
}
