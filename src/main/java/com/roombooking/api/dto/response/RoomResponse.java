package com.roombooking.api.dto.response;

public class RoomResponse {

    private Long id;
    private String name;
    private int capacity;
    private String location;
    private String description;
    private boolean active;

    public RoomResponse(
            Long id,
            String name,
            int capacity,
            String location,
            String description,
            boolean active) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}
