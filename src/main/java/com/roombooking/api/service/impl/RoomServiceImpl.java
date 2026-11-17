package com.roombooking.api.service.impl;

import com.roombooking.api.dto.request.CreateRoomRequest;
import com.roombooking.api.dto.response.RoomResponse;
import com.roombooking.api.entity.Room;
import com.roombooking.api.exception.BusinessRuleException;
import com.roombooking.api.exception.ResourceNotFoundException;
import com.roombooking.api.repository.RoomRepository;
import com.roombooking.api.service.RoomService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service 
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

     @Override
    public RoomResponse createRoom(CreateRoomRequest request) {

        if (roomRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BusinessRuleException(
                    "A room with this name already exists"
            );
        }

        Room room = new Room();
        room.setName(request.getName().trim());
        room.setCapacity(request.getCapacity());
        room.setLocation(request.getLocation());
        room.setDescription(request.getDescription());
        room.setActive(true);

        Room savedRoom = roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(Boolean active) {
 List<Room> rooms;

    if (active == null) {
        rooms = roomRepository.findAll();
    } else {
        rooms = roomRepository.findByActive(active);
    }

    return rooms.stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id
                ));

        return mapToResponse(room);
    }

    private RoomResponse mapToResponse(Room room) {

        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getLocation(),
                room.getDescription(), 
                room.getActive() != null ? room.getActive() : false
        );
    }

}
