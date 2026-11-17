package com.roombooking.api.dto.response;

import java.time.LocalDateTime;

import com.roombooking.api.enums.ReservationStatus;

public class ReservationResponse {

    private Long id;
    private Long roomId;
    private String roomName;
    private Long responsibleUserId;
    private String responsibleUserName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public ReservationResponse(
            Long id,
            Long roomId,
            String roomName,
            Long responsibleUserId,
            String responsibleUserName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ReservationStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.responsibleUserId = responsibleUserId;
        this.responsibleUserName = responsibleUserName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public Long getResponsibleUserId() {
        return responsibleUserId;
    }

    public String getResponsibleUserName() {
        return responsibleUserName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
