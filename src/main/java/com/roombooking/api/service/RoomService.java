package com.roombooking.api.service;

import com.roombooking.api.dto.request.CreateRoomRequest;
import com.roombooking.api.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);

    List<RoomResponse> getAllRooms(Boolean active);

    RoomResponse getRoomById(Long id);
}
