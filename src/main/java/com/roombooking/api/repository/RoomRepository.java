package com.roombooking.api.repository;

import com.roombooking.api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    List<Room> findByActive(boolean active);
}
