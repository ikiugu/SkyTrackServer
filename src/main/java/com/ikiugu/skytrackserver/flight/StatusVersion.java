package com.ikiugu.skytrackserver.flight;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "status_version")
public class StatusVersion {
  @Id
  @Column(name = "flight_number")
  private String flightNumber;

  @Column(nullable = false)
  private Long version;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  // Default constructor
  public StatusVersion() {}

  public StatusVersion(String flightNumber, Long version) {
    this.flightNumber = flightNumber;
    this.version = version;
    this.updatedAt = Instant.now();
  }

  // Getters and Setters
  public String getFlightNumber() {
    return flightNumber;
  }

  public void setFlightNumber(String flightNumber) {
    this.flightNumber = flightNumber;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  @PrePersist
  protected void onCreate() {
    if (updatedAt == null) {
      updatedAt = Instant.now();
    }
  }
}
