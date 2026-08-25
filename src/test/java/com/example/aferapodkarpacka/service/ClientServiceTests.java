package com.example.aferapodkarpacka.service;

import com.example.aferapodkarpacka.model.client.ClientMapper;
import com.example.aferapodkarpacka.exceptions.ClientNotFoundException;
import com.example.aferapodkarpacka.model.client.Client;
import com.example.aferapodkarpacka.model.client.ClientRequest;
import com.example.aferapodkarpacka.model.client.ClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.aferapodkarpacka.repository.ClientRepository;
import com.example.aferapodkarpacka.service.ClientService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientRequest request;
    private ClientResponse response;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        request = new ClientRequest();
        response = new ClientResponse();
        response.setId(1L);
    }

    @Test
    void shouldCreateClientSuccessfully() {
        when(clientMapper.toEntity(request)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponse result = clientService.createClient(request);

        assertThat(result).isNotNull();
        verify(clientRepository).save(client);
    }

    @Test
    void shouldGetClientById() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponse result = clientService.getClientById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowClientNotFoundExceptionWhenClientNotFoundById() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(1L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldGetAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponse(client)).thenReturn(response);

        List<ClientResponse> result = clientService.getAllClients();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponse result = clientService.updateClient(1L, request);

        assertThat(result).isNotNull();
        verify(clientMapper).updateEntity(request, client);
    }

    @Test
    void shouldThrowClientNotFoundExceptionOnUpdateWhenClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(1L, request))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldDeleteClientSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.deleteClient(1L);

        verify(clientRepository).delete(client);
    }

    @Test
    void shouldThrowClientNotFoundExceptionOnDeleteWhenClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.deleteClient(1L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("not found");
    }
}