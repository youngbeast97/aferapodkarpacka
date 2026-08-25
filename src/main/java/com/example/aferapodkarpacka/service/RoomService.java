package com.example.aferapodkarpacka.service;

import com.example.aferapodkarpacka.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.aferapodkarpacka.model.room.Room;
import com.example.aferapodkarpacka.model.room.RoomMapper;
import com.example.aferapodkarpacka.model.room.RoomRequest;
import com.example.aferapodkarpacka.model.room.RoomResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.aferapodkarpacka.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomResponse createRoom(RoomRequest request) {
        validateRoomNameIsAvailable(request.getName(), null);

        Room room = roomMapper.toEntity(request);
        Room savedRoom = roomRepository.save(room);

        return roomMapper.toResponse(savedRoom);
    }

    public RoomResponse getRoomById(Long id) {
        Room room = findRoomById(id);

        return roomMapper.toResponse(room);
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(room -> !room.isOccupied())
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getOccupiedRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::isOccupied)
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getMonitoredRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::isMonitored)
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getUnmonitoredRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(room -> !room.isMonitored())
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getAvailableMonitoredRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(room -> !room.isOccupied())
                .filter(Room::isMonitored)
                .map(roomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getAvailableUnmonitoredRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(room -> !room.isOccupied())
                .filter(room -> !room.isMonitored())
                .map(roomMapper::toResponse)
                .toList();
    }

    public RoomResponse updateRoom(
            Long id,
            RoomRequest request
    ) {
        Room room = findRoomById(id);

        validateRoomNameIsAvailable(
                request.getName(),
                room.getId()
        );

        roomMapper.updateEntity(request, room);

        return roomMapper.toResponse(room);
    }

    public RoomResponse occupyRoom(Long roomId) {
        Room room = findRoomById(roomId);

        if (room.isOccupied()) {
            throw new IllegalStateException(
                    "Room with id: "
                            + roomId
                            + " is already occupied"
            );
        }

        room.setOccupied(true);

        return roomMapper.toResponse(room);
    }


    public RoomResponse releaseRoom(Long roomId) {
        Room room = findRoomById(roomId);

        if (!room.isOccupied()) {
            throw new IllegalStateException(
                    "Room with id: "
                            + roomId
                            + " is already available"
            );
        }

        room.setOccupied(false);

        return roomMapper.toResponse(room);
    }

    @Transactional
    public RoomResponse enableMonitoring(Long roomId) {
        Room room = findRoomById(roomId);

        if (room.isMonitored()) {
            throw new IllegalStateException(
                    "Room with id: "
                            + roomId
                            + " is already monitored"
            );
        }

        if (room.isOccupied()) {
            throw new IllegalStateException(
                    "Monitoring cannot be changed while room with id: "
                            + roomId
                            + " is occupied"
            );
        }

        room.setMonitored(true);

        return roomMapper.toResponse(room);
    }

    public RoomResponse disableMonitoring(Long roomId) {
        Room room = findRoomById(roomId);

        if (!room.isMonitored()) {
            throw new IllegalStateException(
                    "Room with id: "
                            + roomId
                            + " is not monitored"
            );
        }

        if (room.isOccupied()) {
            throw new IllegalStateException(
                    "Monitoring cannot be changed while room with id: "
                            + roomId
                            + " is occupied"
            );
        }

        room.setMonitored(false);

        return roomMapper.toResponse(room);
    }

    public void deleteRoom(Long id) {
        Room room = findRoomById(id);

        if (room.isOccupied()) {
            throw new IllegalStateException(
                    "Occupied room with id: "
                            + id
                            + " cannot be deleted"
            );
        }

        roomRepository.delete(room);
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new RoomNotFoundException(
                                "Room with id: "
                                        + id
                                        + " not found"
                        )
                );
    }

    private void validateRoomNameIsAvailable(
            String roomName,
            Long currentRoomId
    ) {
        roomRepository.findByName(roomName)
                .filter(existingRoom ->
                        currentRoomId == null
                                || !existingRoom.getId()
                                .equals(currentRoomId)
                )
                .ifPresent(existingRoom -> {
                    throw new IllegalStateException(
                            "Room with name: "
                                    + roomName
                                    + " already exists"
                    );
                });
    }
}