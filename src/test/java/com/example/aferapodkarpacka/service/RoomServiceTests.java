package com.example.aferapodkarpacka.service;

import exceptions.RoomNotFoundException;
import model.room.Room;
import model.room.RoomMapper;
import model.room.RoomRequest;
import model.room.RoomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.RoomRepository;
import service.RoomService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RoomServiceTests {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private RoomRequest request;
    private RoomResponse response;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(1L);
        request = new RoomRequest();
        response = new RoomResponse();
        response.setId(1L);
    }

    @Test
    void shouldCreateRoomSuccessfully() {
        when(roomMapper.toEntity(request)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toResponse(room)).thenReturn(response);

        RoomResponse result = roomService.createRoom(request);

        assertThat(result).isNotNull();
        verify(roomRepository).save(room);
    }

    @Test
    void shouldGetRoomById() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toResponse(room)).thenReturn(response);

        RoomResponse result = roomService.getRoomById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowRoomNotFoundExceptionWhenRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(1L))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldGetAllRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(roomMapper.toResponse(room)).thenReturn(response);

        List<RoomResponse> result = roomService.getAllRooms();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldUpdateRoomSuccessfully() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toResponse(room)).thenReturn(response);

        RoomResponse result = roomService.updateRoom(1L, request);

        assertThat(result).isNotNull();
        verify(roomMapper).updateEntity(request, room);
    }

    @Test
    void shouldThrowRoomNotFoundExceptionOnUpdateWhenRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateRoom(1L, request))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldDeleteRoomSuccessfully() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(roomRepository).delete(room);
    }

    @Test
    void shouldThrowRoomNotFoundExceptionOnDeleteWhenRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.deleteRoom(1L))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("not found");
    }
}