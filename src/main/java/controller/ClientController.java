package controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.client.ClientRequest;
import model.client.ClientResponse;
import org.springframework.web.bind.annotation.*;
import service.ClientService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ClientResponse createClient(
            @Valid @RequestBody ClientRequest request) {

        return clientService.createClient(request);
    }

    @GetMapping("/{id}")
    public ClientResponse getClientById(
            @PathVariable Long id) {

        return clientService.getClientById(id);
    }

    @GetMapping
    public List<ClientResponse> getAllClients() {

        return clientService.getAllClients();
    }

    @PutMapping("/{id}")
    public ClientResponse updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {

        return clientService.updateClient(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(
            @PathVariable Long id) {

        clientService.deleteClient(id);
    }
}