package com.ikiugu.skytrackserver.booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
  List<Booking> findByUserId(UUID userId);

  Optional<Booking> findByUserIdAndFlightId(UUID userId, UUID flightId);
}
