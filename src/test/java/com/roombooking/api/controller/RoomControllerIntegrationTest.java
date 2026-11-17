package com.roombooking.api.controller;

import com.roombooking.api.entity.Room;
import com.roombooking.api.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();

        Room activeRoom = new Room();
        activeRoom.setName("Integration Test Room");
        activeRoom.setCapacity(10);
        activeRoom.setLocation("First Floor");
        activeRoom.setDescription("Room created for integration testing");
        activeRoom.setActive(true);

        Room inactiveRoom = new Room();
        inactiveRoom.setName("Inactive Test Room");
        inactiveRoom.setCapacity(5);
        inactiveRoom.setLocation("Second Floor");
        inactiveRoom.setDescription("Inactive room for testing");
        inactiveRoom.setActive(false);

        roomRepository.save(activeRoom);
        roomRepository.save(inactiveRoom);
    }

    @Test
    void getActiveRooms_shouldReturnOnlyActiveRooms() throws Exception {
        mockMvc.perform(get("/api/rooms?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name")
                        .value("Integration Test Room"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getInactiveRooms_shouldReturnOnlyInactiveRooms() throws Exception {
        mockMvc.perform(get("/api/rooms?active=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name")
                        .value("Inactive Test Room"))
                .andExpect(jsonPath("$[0].active").value(false));
    }

    @Test
    void getAllRooms_shouldReturnAllRooms() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
