package com.roombooking.api.service.impl;

import com.roombooking.api.dto.request.CreateReservationRequest;
import com.roombooking.api.dto.response.ReservationResponse;
import com.roombooking.api.entity.*;
import com.roombooking.api.enums.ReservationStatus;
import com.roombooking.api.exception.BusinessRuleException;
import com.roombooking.api.exception.ResourceNotFoundException;
import com.roombooking.api.repository.ReservationRepository;
import com.roombooking.api.repository.RoomRepository;
import com.roombooking.api.repository.UserRepository;
import com.roombooking.api.service.ReservationService;
import com.roombooking.api.specification.ReservationSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ReservationServiceImpl  implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReservationResponse createReservation(
            CreateReservationRequest request,
            String userEmail) {

        // Validate reservation dates
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessRuleException(
                    "End time must be after start time");
        }

        // Find room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + request.getRoomId()));

        if (!room.getActive()) {
            throw new BusinessRuleException("Room is not active");
        }

        // Find authenticated user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        // Check room availability
        boolean roomIsReserved =
                reservationRepository
                        .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                                room.getId(),
                                ReservationStatus.CONFIRMED,
                                request.getEndTime(),
                                request.getStartTime()
                        );

        if (roomIsReserved) {
            throw new BusinessRuleException(
                    "Room is already reserved for the requested time",
                    HttpStatus.CONFLICT
            );
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setResponsibleUser(user);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getReservations(
            Long roomId,
            LocalDate date,
            Pageable pageable) {

        Specification<Reservation> specification =
                Specification.allOf(
                        ReservationSpecification.hasRoomId(roomId),
                        ReservationSpecification.overlapsDate(date)
                );

        return reservationRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + id));

        return mapToResponse(reservation);
    }

    @Override
    public void cancelReservation(Long id, String userEmail) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + id));

        // Only the user who created the reservation can cancel it
        if (!reservation.getResponsibleUser().getEmail()
                .equalsIgnoreCase(userEmail)) {
            throw new BusinessRuleException(
                    "You can only cancel your own reservations",
                    HttpStatus.FORBIDDEN
            );
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getRoom().getName(),
                reservation.getResponsibleUser().getId(),
                reservation.getResponsibleUser().getName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }

}
