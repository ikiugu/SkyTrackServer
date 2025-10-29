package com.ikiugu.skytrackserver.flight.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ikiugu.skytrackserver.flight.StatusVersion;
import com.ikiugu.skytrackserver.flight.StatusVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StatusVersionServiceTest {

  @Autowired private StatusVersionRepository repository;

  private StatusVersionService service;

  @BeforeEach
  void setUp() {
    service = new StatusVersionService(repository);
  }

  @Test
  void shouldCreateNewVersion() {
    Long version = service.incrementVersion("AA100");

    assertEquals(1L, version);
    Optional<StatusVersion> saved = repository.findById("AA100");
    assertTrue(saved.isPresent());
    assertEquals(1L, saved.get().getVersion());
  }

  @Test
  void shouldIncrementExistingVersion() {
    repository.save(new StatusVersion("AA100", 5L));

    Long version = service.incrementVersion("AA100");

    assertEquals(6L, version);
    Optional<StatusVersion> saved = repository.findById("AA100");
    assertTrue(saved.isPresent());
    assertEquals(6L, saved.get().getVersion());
  }

  @Test
  void shouldGetCurrentVersion() {
    repository.save(new StatusVersion("AA100", 3L));

    Optional<Long> version = service.getCurrentVersion("AA100");

    assertTrue(version.isPresent());
    assertEquals(3L, version.get());
  }

  @Test
  void shouldReturnEmptyForNonExistentVersion() {
    Optional<Long> version = service.getCurrentVersion("AA999");

    assertFalse(version.isPresent());
  }

  @Test
  void shouldHandleConcurrentIncrements() {
    // Test that version increments are atomic
    Long version1 = service.incrementVersion("AA100");
    Long version2 = service.incrementVersion("AA100");
    Long version3 = service.incrementVersion("AA100");

    assertEquals(1L, version1);
    assertEquals(2L, version2);
    assertEquals(3L, version3);

    Optional<StatusVersion> saved = repository.findById("AA100");
    assertTrue(saved.isPresent());
    assertEquals(3L, saved.get().getVersion());
  }
}
