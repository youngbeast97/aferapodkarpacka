package com.example.aferapodkarpacka.controller;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.example.aferapodkarpacka.model.room.RoomRequest;
import com.example.aferapodkarpacka.model.room.RoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.aferapodkarpacka.service.RoomService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable Long id) {

        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid
            @NotNull
            @RequestBody RoomRequest request) {

        return ResponseEntity
                .status(201)
                .body(roomService.createRoom(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid
            @NotNull
            @RequestBody RoomRequest request) {

        return ResponseEntity.ok(
                roomService.updateRoom(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return ResponseEntity.noContent().build();
    }
}