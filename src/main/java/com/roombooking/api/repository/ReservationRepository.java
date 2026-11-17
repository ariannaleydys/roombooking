package com.roombooking.api.repository;

import com.roombooking.api.entity.Reservation;
import com.roombooking.api.enums.ReservationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> , JpaSpecificationExecutor<Reservation> {

    boolean existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId,
            ReservationStatus status,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );
}
