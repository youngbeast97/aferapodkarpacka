package service;

import com.example.aferapodkarpacka.model.client.ClientMapper;
import exceptions.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import model.client.Client;
import model.client.ClientRequest;
import model.client.ClientResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ClientRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final BigDecimal FIRST_DEBT_MULTIPLIER =
            new BigDecimal("4.50");

    private static final BigDecimal NEXT_DEBT_MULTIPLIER =
            new BigDecimal("1.00");

    private static final BigDecimal DEBT_TO_SALARY_LIMIT =
            new BigDecimal("6.00");

    private static final int INFECTION_DURATION_DAYS = 14;

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponse createClient(ClientRequest request) {
        Client client = clientMapper.toEntity(request);

        if (client.getDebt() == null) {
            client.setDebt(BigDecimal.ZERO);
        }

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponse(savedClient);
    }

    public ClientResponse getClientById(Long id) {
        Client client = findClientById(id);

        return clientMapper.toResponse(client);
    }

    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getActiveClients() {
        return clientRepository.findAll()
                .stream()
                .filter(Client::isActive)
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getInactiveClients() {
        return clientRepository.findAll()
                .stream()
                .filter(client -> !client.isActive())
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getInfectedClients() {
        LocalDateTime now = LocalDateTime.now();

        return clientRepository.findAll()
                .stream()
                .filter(client -> client.getInfectedUntil() != null)
                .filter(client ->
                        client.getInfectedUntil().isAfter(now)
                )
                .map(clientMapper::toResponse)
                .toList();
    }

    public List<ClientResponse> getClientsWithDebt() {
        return clientRepository.findAll()
                .stream()
                .filter(client -> client.getDebt() != null)
                .filter(client ->
                        client.getDebt()
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .map(clientMapper::toResponse)
                .toList();
    }

    @Transactional
    public ClientResponse updateClient(
            Long id,
            ClientRequest request
    ) {
        Client client = findClientById(id);

        clientMapper.updateEntity(request, client);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponse(savedClient);
    }

    public void deleteClient(Long id) {
        Client client = findClientById(id);

        clientRepository.delete(client);
    }

    @Transactional
    public ClientResponse infectClient(Long clientId) {
        Client client = findClientById(clientId);

        client.setInfectedUntil(
                LocalDateTime.now()
                        .plusDays(INFECTION_DURATION_DAYS)
        );

        client.setActive(false);

        return clientMapper.toResponse(client);
    }

    @Transactional
    public void restoreRecoveredClients() {
        LocalDateTime now = LocalDateTime.now();

        List<Client> recoveredClients =
                clientRepository.findByInfectedUntilBefore(now);

        for (Client client : recoveredClients) {
            if (client.getInfectedUntil() == null) {
                continue;
            }

            client.setInfectedUntil(null);

            if (!hasCriticalDebt(client)) {
                client.setActive(true);
            }
        }
    }

    @Transactional
    public ClientResponse addDebt(
            Long clientId,
            BigDecimal amount
    ) {
        validatePositiveAmount(amount);

        Client client = findClientById(clientId);

        BigDecimal currentDebt = getDebtOrZero(client);

        client.setDebt(currentDebt.add(amount));

        deactivateWhenDebtIsTooHigh(client);

        return clientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponse repayDebt(
            Long clientId,
            BigDecimal amountToPay
    ) {
        validatePositiveAmount(amountToPay);

        Client client = findClientById(clientId);

        BigDecimal currentDebt = getDebtOrZero(client);

        BigDecimal newDebt = currentDebt.subtract(amountToPay);

        if (newDebt.compareTo(BigDecimal.ZERO) < 0) {
            newDebt = BigDecimal.ZERO;
        }

        client.setDebt(
                newDebt.setScale(2, RoundingMode.HALF_UP)
        );

        if (client.getDebt().compareTo(BigDecimal.ZERO) == 0
                && !isCurrentlyInfected(client)) {

            client.setActive(true);
        }

        return clientMapper.toResponse(client);
    }

    @Transactional
    public void increaseDebts() {
        List<Client> clients = clientRepository.findAll();

        for (Client client : clients) {
            BigDecimal currentDebt = getDebtOrZero(client);

            if (currentDebt.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal multiplier;

            if (currentDebt.compareTo(client.getSalary()) <= 0) {
                multiplier = FIRST_DEBT_MULTIPLIER;
            } else {
                multiplier = NEXT_DEBT_MULTIPLIER;
            }

            BigDecimal increasedDebt = currentDebt
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            client.setDebt(increasedDebt);

            deactivateWhenDebtIsTooHigh(client);
        }
    }

    @Transactional
    public void evaluateClientsDebtStatus() {
        List<Client> clients = clientRepository.findAll();

        for (Client client : clients) {
            deactivateWhenDebtIsTooHigh(client);
        }
    }

    @Transactional
    public ClientResponse activateClient(Long clientId) {
        Client client = findClientById(clientId);

        if (isCurrentlyInfected(client)) {
            throw new IllegalStateException(
                    "Client with id: "
                            + clientId
                            + " is still infected"
            );
        }

        if (hasCriticalDebt(client)) {
            throw new IllegalStateException(
                    "Client with id: "
                            + clientId
                            + " has too much debt"
            );
        }

        client.setActive(true);

        return clientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponse deactivateClient(Long clientId) {
        Client client = findClientById(clientId);

        client.setActive(false);

        return clientMapper.toResponse(client);
    }

    private Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Client with id: "
                                        + id
                                        + " not found"
                        )
                );
    }

    private void deactivateWhenDebtIsTooHigh(Client client) {
        if (hasCriticalDebt(client)) {
            client.setActive(false);
        }
    }

    private boolean hasCriticalDebt(Client client) {
        if (client.getSalary() == null
                || client.getDebt() == null) {
            return false;
        }

        BigDecimal debtLimit = client.getSalary()
                .multiply(DEBT_TO_SALARY_LIMIT);

        return client.getDebt()
                .compareTo(debtLimit) >= 0;
    }

    private boolean isCurrentlyInfected(Client client) {
        return client.getInfectedUntil() != null
                && client.getInfectedUntil()
                .isAfter(LocalDateTime.now());
    }

    private BigDecimal getDebtOrZero(Client client) {
        if (client.getDebt() == null) {
            return BigDecimal.ZERO;
        }

        return client.getDebt();
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }
}