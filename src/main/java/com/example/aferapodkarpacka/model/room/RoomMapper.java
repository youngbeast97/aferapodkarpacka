package com.example.aferapodkarpacka.model.room;


import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {
        if (request == null) {
            return null;
        }

        Room room = new Room();
        room.setName(request.getName());
        room.setMonitored(request.isMonitored());

        room.setOccupied(false);

        return room;
    }

    public void updateEntity(RoomRequest request, Room room) {
        if (request == null || room == null) {
            return;
        }

        room.setName(request.getName());
        room.setMonitored(request.isMonitored());
    }

    public RoomResponse toResponse(Room room) {
        if (room == null) {
            return null;
        }

        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setMonitored(room.isMonitored());
        response.setOccupied(room.isOccupied());

        return response;
    }
}