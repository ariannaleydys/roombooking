package com.roombooking.api.specification;

import com.roombooking.api.entity.Reservation;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class ReservationSpecification {

     private ReservationSpecification() {
    }

    public static Specification<Reservation> hasRoomId(Long roomId) {

        return (root, query, criteriaBuilder) -> {

            if (roomId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("room").get("id"),
                    roomId
            );
        };
    }

    public static Specification<Reservation> overlapsDate(LocalDate date) {

        return (root, query, criteriaBuilder) -> {

            if (date == null) {
                return criteriaBuilder.conjunction();
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime nextDay = date.plusDays(1).atStartOfDay();

            return criteriaBuilder.and(
                    criteriaBuilder.lessThan(
                            root.get("startTime"),
                            nextDay
                    ),
                    criteriaBuilder.greaterThan(
                            root.get("endTime"),
                            startOfDay
                    )
            );
        };
    }
}
