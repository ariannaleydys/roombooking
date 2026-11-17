package com.roombooking.api.repository;

import com.roombooking.api.entity.Reservation;
import com.roombooking.api.enums.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId,
            ReservationStatus status,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );
}
