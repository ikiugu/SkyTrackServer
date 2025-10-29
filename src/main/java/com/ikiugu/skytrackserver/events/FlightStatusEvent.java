package com.ikiugu.skytrackserver.events;

import java.io.Serializable;
import java.time.Instant;

public record FlightStatusEvent(
    String flightNumber,
    String status,
    String newGate,
    Integer delayMinutes,
    Long version,
    Instant occurredAt,
    boolean isUrgent)
    implements Serializable {}
