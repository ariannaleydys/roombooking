package com.roombooking.api.controller;

import com.roombooking.api.dto.request.CreateReservationRequest;
import com.roombooking.api.dto.response.ReservationResponse;
import com.roombooking.api.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import com.roombooking.api.exception.BusinessRuleException;

import org.springframework.data.domain.PageRequest;

import java.util.Set;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

     private final ReservationService reservationService;
     private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id",
        "startTime",
        "endTime",
        "createdAt",
        "status"
);

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication authentication) {

        ReservationResponse response = reservationService.createReservation(
                request,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getReservations(
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) LocalDate date,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "startTime,asc") String sort) {

        if (page < 0) {
            throw new BusinessRuleException(
                    "Page number must be greater than or equal to 0"
            );
        }

        if (size < 1 || size > 100) {
            throw new BusinessRuleException(
                    "Page size must be between 1 and 100"
            );
        }

        String[] sortParameters = sort.split(",", -1);

        if (sortParameters.length != 2) {
            throw new BusinessRuleException(
                    "Invalid sort format. Expected: property,direction"
            );
        }

        String property = sortParameters[0].trim();
        String direction = sortParameters[1].trim();

        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            throw new BusinessRuleException(
                    "Invalid sort property: " + property
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException(
                    "Invalid sort direction. Use 'asc' or 'desc'"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, property)
        );

        return ResponseEntity.ok(
                reservationService.getReservations(roomId, date, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService.getReservationById(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {

        reservationService.cancelReservation(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

}
