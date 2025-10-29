package com.ikiugu.skytrackserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.flyway.enabled=false",
      "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers:localhost:9092}"
    })
class SkyTrackServerApplicationTests {

  @Test
  void contextLoads() {}
}
