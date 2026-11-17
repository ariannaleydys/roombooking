package com.roombooking.api.service.impl;

import com.roombooking.api.dto.request.CreateReservationRequest;
import com.roombooking.api.dto.response.ReservationResponse;
import com.roombooking.api.entity.Reservation;
import com.roombooking.api.enums.ReservationStatus;
import com.roombooking.api.exception.BusinessRuleException;
import com.roombooking.api.exception.ResourceNotFoundException;
import com.roombooking.api.entity.Room;
import com.roombooking.api.entity.User;
import com.roombooking.api.repository.ReservationRepository;
import com.roombooking.api.repository.RoomRepository;
import com.roombooking.api.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
     @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void createReservation_shouldReturnReservation_whenDataIsValid() {

        // Arrange
        Long roomId = 1L;
        String userEmail = "user@test.com";

        LocalDateTime startTime =
                LocalDateTime.of(2030, 5, 10, 9, 0);

        LocalDateTime endTime =
                LocalDateTime.of(2030, 5, 10, 10, 0);

        Room room = new Room();
        room.setId(roomId);
        room.setName("Meeting Room");
        room.setActive(true);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail(userEmail);

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setRoomId(roomId);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(reservationRepository
                .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        roomId,
                        ReservationStatus.CONFIRMED,
                        endTime,
                        startTime))
                .thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation = invocation.getArgument(0);
                    reservation.setId(1L);
                    return reservation;
                });

        // Act
        ReservationResponse response =
                reservationService.createReservation(request, userEmail);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(roomId, response.getRoomId());
        assertEquals("Meeting Room", response.getRoomName());
        assertEquals(1L, response.getResponsibleUserId());
        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());

        verify(reservationRepository, times(1))
                .save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowConflict_whenRoomIsAlreadyReserved() {

        // Arrange
        Long roomId = 1L;
        String userEmail = "user@test.com";

        LocalDateTime startTime =
                LocalDateTime.of(2030, 5, 10, 9, 0);

        LocalDateTime endTime =
                LocalDateTime.of(2030, 5, 10, 10, 0);

        Room room = new Room();
        room.setId(roomId);
        room.setName("Meeting Room");
        room.setActive(true);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail(userEmail);

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setRoomId(roomId);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(reservationRepository
                .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        roomId,
                        ReservationStatus.CONFIRMED,
                        endTime,
                        startTime))
                .thenReturn(true);

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> reservationService.createReservation(request, userEmail)
        );

        assertEquals(
                "Room is already reserved for the requested time",
                exception.getMessage()
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowNotFound_whenRoomDoesNotExist() {

        // Arrange
        Long roomId = 99L;
        String userEmail = "user@test.com";

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setRoomId(roomId);
        request.setStartTime(
                LocalDateTime.of(2030, 5, 10, 9, 0)
        );
        request.setEndTime(
                LocalDateTime.of(2030, 5, 10, 10, 0)
        );

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, userEmail)
        );

        assertEquals(
                "Room not found with ID: " + roomId,
                exception.getMessage()
        );

        verify(userRepository, never()).findByEmail(anyString());

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }


    @Test
    void createReservation_shouldThrowBadRequest_whenRoomIsInactive() {

        // Arrange
        Long roomId = 1L;
        String userEmail = "user@test.com";

        Room room = new Room();
        room.setId(roomId);
        room.setName("Meeting Room");
        room.setActive(false);

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setRoomId(roomId);
        request.setStartTime(
                LocalDateTime.of(2030, 5, 10, 9, 0)
        );
        request.setEndTime(
                LocalDateTime.of(2030, 5, 10, 10, 0)
        );

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> reservationService.createReservation(request, userEmail)
        );

        assertEquals("Room is not active", exception.getMessage());

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(userRepository, never()).findByEmail(anyString());

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }


    @Test
    void createReservation_shouldThrowNotFound_whenUserDoesNotExist() {

        // Arrange
        Long roomId = 1L;
        String userEmail = "unknown@test.com";

        Room room = new Room();
        room.setId(roomId);
        room.setName("Meeting Room");
        room.setActive(true);

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setRoomId(roomId);
        request.setStartTime(
                LocalDateTime.of(2030, 5, 10, 9, 0)
        );
        request.setEndTime(
                LocalDateTime.of(2030, 5, 10, 10, 0)
        );

        when(roomRepository.findById(roomId))
                .thenReturn(Optional.of(room));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, userEmail)
        );

        assertEquals(
                "Authenticated user not found",
                exception.getMessage()
        );

        verify(reservationRepository, never())
                .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        anyLong(),
                        any(ReservationStatus.class),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }



}
