package com.example.aferapodkarpacka.model.client;

import model.client.Client;
import model.client.ClientRequest;
import model.client.ClientResponse;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    // CREATE ENTITY
    public Client toEntity(ClientRequest request) {
        if (request == null) return null;

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setAge(request.getAge());
        client.setSocialStatus(request.getSocialStatus());
        client.setParty(request.getParty());

        client.setMonthlyBudget(request.getMonthlyBudget());
        client.setSalary(request.getSalary());
        client.setDebt(request.getDebt());

        client.setActive(request.isActive());

        client.setInfectedUntil(null);

        return client;
    }

    public void updateEntity(ClientRequest request, Client client) {
        if (request == null || client == null) return;

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setAge(request.getAge());
        client.setSocialStatus(request.getSocialStatus());
        client.setParty(request.getParty());

        client.setMonthlyBudget(request.getMonthlyBudget());
        client.setSalary(request.getSalary());

        client.setDebt(request.getDebt());

        client.setActive(request.isActive());
    }

    public ClientResponse toResponse(Client client) {
        if (client == null) return null;

        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setAge(client.getAge());
        response.setSocialStatus(client.getSocialStatus());
        response.setParty(client.getParty());
        response.setMonthlyBudget(client.getMonthlyBudget());
        response.setSalary(client.getSalary());
        response.setDebt(client.getDebt());
        response.setActive(client.isActive());
        response.setInfectedUntil(client.getInfectedUntil());

        return response;
    }
}