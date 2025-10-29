package com.ikiugu.skytrackserver.flight;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "flights",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"flight_number", "flight_date"})})
public class Flight {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "flight_number", nullable = false)
  private String flightNumber;

  @Column(name = "dep_iata", nullable = false, length = 3)
  private String depIata;

  @Column(name = "arr_iata", nullable = false, length = 3)
  private String arrIata;

  @Column(name = "dep_time", nullable = false)
  private Instant depTime;

  @Column(name = "arr_time", nullable = false)
  private Instant arrTime;

  @Column(name = "gate")
  private String gate;

  @Column(nullable = false)
  private String status;

  @Column(name = "flight_date", nullable = false)
  private LocalDate flightDate;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version private Long version;

  // Default constructor
  public Flight() {}

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFlightNumber() {
    return flightNumber;
  }

  public void setFlightNumber(String flightNumber) {
    this.flightNumber = flightNumber;
  }

  public String getDepIata() {
    return depIata;
  }

  public void setDepIata(String depIata) {
    this.depIata = depIata;
  }

  public String getArrIata() {
    return arrIata;
  }

  public void setArrIata(String arrIata) {
    this.arrIata = arrIata;
  }

  public Instant getDepTime() {
    return depTime;
  }

  public void setDepTime(Instant depTime) {
    this.depTime = depTime;
  }

  public Instant getArrTime() {
    return arrTime;
  }

  public void setArrTime(Instant arrTime) {
    this.arrTime = arrTime;
  }

  public String getGate() {
    return gate;
  }

  public void setGate(String gate) {
    this.gate = gate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getFlightDate() {
    return flightDate;
  }

  public void setFlightDate(LocalDate flightDate) {
    this.flightDate = flightDate;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
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
