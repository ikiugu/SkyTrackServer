package com.ikiugu.skytrackserver.flight.service;

import com.ikiugu.skytrackserver.flight.StatusVersion;
import com.ikiugu.skytrackserver.flight.StatusVersionRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatusVersionService {
  private final StatusVersionRepository statusVersionRepository;

  public StatusVersionService(StatusVersionRepository statusVersionRepository) {
    this.statusVersionRepository = statusVersionRepository;
  }

  @Transactional
  public Long incrementVersion(String flightNumber) {
    Optional<StatusVersion> existing = statusVersionRepository.findById(flightNumber);

    if (existing.isPresent()) {
      StatusVersion version = existing.get();
      version.setVersion(version.getVersion() + 1);
      statusVersionRepository.save(version);
      return version.getVersion();
    } else {
      StatusVersion newVersion = new StatusVersion(flightNumber, 1L);
      statusVersionRepository.save(newVersion);
      return 1L;
    }
  }

  public Optional<Long> getCurrentVersion(String flightNumber) {
    return statusVersionRepository.findById(flightNumber).map(StatusVersion::getVersion);
  }
}
