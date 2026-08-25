package com.example.aferapodkarpacka.service;


import com.example.aferapodkarpacka.exceptions.ClientNotFoundException;
import com.example.aferapodkarpacka.exceptions.RoomNotFoundException;
import com.example.aferapodkarpacka.exceptions.SessionNotFoundException;
import com.example.aferapodkarpacka.exceptions.WorkerNotFoundException;
import com.example.aferapodkarpacka.model.client.Client;
import com.example.aferapodkarpacka.model.room.Room;
import com.example.aferapodkarpacka.model.session.Session;
import com.example.aferapodkarpacka.model.session.SessionMapper;
import com.example.aferapodkarpacka.model.session.SessionRequest;
import com.example.aferapodkarpacka.model.session.SessionResponse;
import com.example.aferapodkarpacka.model.worker.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.aferapodkarpacka.repository.ClientRepository;
import com.example.aferapodkarpacka.repository.RoomRepository;
import com.example.aferapodkarpacka.repository.SessionRepository;
import com.example.aferapodkarpacka.repository.WorkerRepository;
import com.example.aferapodkarpacka.service.SessionService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SessionServiceTests {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private WorkerRepository workerRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionService sessionService;

    private SessionRequest sessionRequest;
    private Client client;
    private Worker worker;
    private Room room;
    private Session session;
    private SessionResponse sessionResponse;

    @BeforeEach
    void setUp() {
        sessionRequest = new SessionRequest();
        sessionRequest.setClientId(1L);
        sessionRequest.setWorkerId(2L);
        sessionRequest.setRoomId(3L);

        client = new Client();
        client.setId(1L);

        worker = new Worker();
        worker.setId(2L);
        worker.setHourlyRate(BigDecimal.valueOf(150));
        worker.setInfected(false);

        room = new Room();
        room.setId(3L);

        session = new Session();
        sessionResponse = new SessionResponse();
    }

    @Test
    void shouldStartSessionSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(workerRepository.findById(2L)).thenReturn(Optional.of(worker));
        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toResponse(any(Session.class))).thenReturn(sessionResponse);

        SessionResponse result = sessionService.startSession(sessionRequest);

        assertThat(result).isNotNull();
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void shouldThrowClientNotFoundExceptionWhenClientMissingOnStartSession() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.startSession(sessionRequest))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client with provided ID not recognized");

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowWorkerNotFoundExceptionWhenWorkerMissingOnStartSession() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(workerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.startSession(sessionRequest))
                .isInstanceOf(WorkerNotFoundException.class)
                .hasMessage("not found worker that you asked for");

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowRoomNotFoundExceptionWhenRoomMissingOnStartSession() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(workerRepository.findById(2L)).thenReturn(Optional.of(worker));
        when(roomRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.startSession(sessionRequest))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("not found room that you asked for");

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldGetSessionById() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionMapper.toResponse(session)).thenReturn(sessionResponse);

        SessionResponse result = sessionService.getSessionById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowSessionNotFoundExceptionWhenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSessionById(1L))
                .isInstanceOf(SessionNotFoundException.class)
                .hasMessage("not found");
    }
}