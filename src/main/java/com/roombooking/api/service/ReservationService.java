package com.roombooking.api.service;

import com.roombooking.api.dto.request.CreateReservationRequest;
import com.roombooking.api.dto.response.ReservationResponse;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {
 ReservationResponse createReservation(CreateReservationRequest request,String userEmail);

    Page<ReservationResponse> getReservations(Long roomId,LocalDate date,Pageable pageable);

    ReservationResponse getReservationById(Long id);

    void cancelReservation(Long id, String userEmail);
}
