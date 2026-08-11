package service;

import exceptions.ClientNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.SessionNotFoundException;
import exceptions.WorkerNotFoundException;
import lombok.RequiredArgsConstructor;
import model.client.Client;
import model.enums.SocialStatus;
import model.room.Room;
import model.session.Session;
import model.session.SessionMapper;
import model.session.SessionRequest;
import model.session.SessionResponse;
import model.worker.Worker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ClientRepository;
import repository.RoomRepository;
import repository.SessionRepository;
import repository.WorkerRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final int DEFAULT_SESSION_DURATION_MINUTES = 60;
    private static final int INFECTION_DURATION_DAYS = 14;

    private final SessionRepository sessionRepository;
    private final ClientRepository clientRepository;
    private final WorkerRepository workerRepository;
    private final RoomRepository roomRepository;
    private final SessionMapper sessionMapper;

    @Transactional
    public SessionResponse startSession(SessionRequest request) {
        Client client = findClientById(request.getClientId());
        Worker worker = findWorkerById(request.getWorkerId());
        Room room = findRoomById(request.getRoomId());

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(
                DEFAULT_SESSION_DURATION_MINUTES
        );

        validateClient(client);
        validateRoom(room);
        validateWorkerAvailability(worker, startTime);
        validateRoomAvailability(room, startTime);

        BigDecimal totalCostofSession = worker.getHourlyRate();

        chargeClient(client, totalCostofSession);

        Session session = new Session();
        session.setClient(client);
        session.setWorker(worker);
        session.setRoom(room);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setDurationMinutes(
                DEFAULT_SESSION_DURATION_MINUTES
        );
        session.setTotalCost(totalCostofSession);

        infectClientIfNecessary(client, worker, session);

        room.setOccupied(true);

        BigDecimal currentEarnings = worker.getEarningsTotal();

        if (currentEarnings == null) {
            currentEarnings = BigDecimal.ZERO;
        }

        worker.setEarningsTotal(
                currentEarnings.add(totalCostofSession)
        );

        Session savedSession = sessionRepository.save(session);

        return sessionMapper.toResponse(savedSession);
    }

    public SessionResponse getSessionById(Long id) {
        Session session = findSessionById(id);

        return sessionMapper.toResponse(session);
    }

    public List<SessionResponse> getAllSessionsInBurdel() {
        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }

    public List<SessionResponse> getSessionsByClient(
            Long clientId
    ) {
        findClientById(clientId);

        return sessionRepository.findAll()
                .stream()
                .filter(session ->
                        session.getClient()
                                .getId()
                                .equals(clientId)
                )
                .map(sessionMapper::toResponse)
                .toList();
    }

    public List<SessionResponse> getSessionsByWorker(
            Long workerId
    ) {
        findWorkerById(workerId);

        return sessionRepository.findAll()
                .stream()
                .filter(session ->
                        session.getWorker()
                                .getId()
                                .equals(workerId)
                )
                .map(sessionMapper::toResponse)
                .toList();
    }

    public List<SessionResponse> getSessionsByRoom(
            Long roomId
    ) {
        findRoomById(roomId);

        return sessionRepository.findAll()
                .stream()
                .filter(session ->
                        session.getRoom()
                                .getId()
                                .equals(roomId)
                )
                .map(sessionMapper::toResponse)
                .toList();
    }

    public List<SessionResponse> getInfectedSessions() {
        return sessionRepository.findAll()
                .stream()
                .filter(Session::isInfected)
                .map(sessionMapper::toResponse)
                .toList();
    }

    @Transactional
    public SessionResponse finishSession(Long sessionId) {
        Session session = findSessionById(sessionId);

        LocalDateTime now = LocalDateTime.now();

        if (session.getEndTime() != null
                && session.getEndTime().isBefore(now)) {

            session.getRoom().setOccupied(false);

            return sessionMapper.toResponse(session);
        }

        int realDuration = calculateDurationMinutes(
                session.getStartTime(),
                now
        );

        session.setEndTime(now);
        session.setDurationMinutes(realDuration);
        session.getRoom().setOccupied(false);

        return sessionMapper.toResponse(session);
    }

    @Transactional
    public void releaseRoomsAfterFinishedSessions() {
        LocalDateTime now = LocalDateTime.now();

        List<Session> finishedSessions =
                sessionRepository.findAll()
                        .stream()
                        .filter(session ->
                                session.getEndTime() != null
                        )
                        .filter(session ->
                                !session.getEndTime().isAfter(now)
                        )
                        .filter(session ->
                                session.getRoom().isOccupied()
                        )
                        .toList();

        for (Session session : finishedSessions) {
            Room room = session.getRoom();

            boolean anotherActiveSessionExists =
                    sessionRepository.findAll()
                            .stream()
                            .filter(otherSession ->
                                    !otherSession.getId()
                                            .equals(session.getId())
                            )
                            .filter(otherSession ->
                                    otherSession.getRoom()
                                            .getId()
                                            .equals(room.getId())
                            )
                            .anyMatch(otherSession ->
                                    otherSession.getEndTime() != null
                                            && otherSession.getEndTime()
                                            .isAfter(now)
                            );

            if (!anotherActiveSessionExists) {
                room.setOccupied(false);
            }
        }
    }

    public void deleteSession(Long sessionId) {
        Session session = findSessionById(sessionId);

        if (session.getEndTime() != null
                && session.getEndTime()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalStateException(
                    "Active session with id: "
                            + sessionId
                            + " cannot be deleted"
            );
        }

        sessionRepository.delete(session);
    }

    private void validateClient(Client client) {
        if (!client.isActive()) {
            throw new IllegalStateException(
                    "Client with id: "
                            + client.getId()
                            + " is not active"
            );
        }

        if (client.getInfectedUntil() != null
                && client.getInfectedUntil()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalStateException(
                    "Client with id: "
                            + client.getId()
                            + " is temporarily unavailable until: "
                            + client.getInfectedUntil()
            );
        }
    }

    private void validateRoom(Room room) {
        if (room.isOccupied()) {
            throw new IllegalStateException(
                    "Room with id: "
                            + room.getId()
                            + " is currently occupied"
            );
        }
    }

    private void validateWorkerAvailability(
            Worker worker,
            LocalDateTime startTime
    ) {
        boolean workerHasActiveSession =
                sessionRepository.findAll()
                        .stream()
                        .filter(session ->
                                session.getWorker()
                                        .getId()
                                        .equals(worker.getId())
                        )
                        .anyMatch(session ->
                                session.getEndTime() != null
                                        && session.getEndTime()
                                        .isAfter(startTime)
                        );

        if (workerHasActiveSession) {
            throw new IllegalStateException(
                    "Worker with id: "
                            + worker.getId()
                            + " is currently busy"
            );
        }
    }

    private void validateRoomAvailability(
            Room room,
            LocalDateTime startTime
    ) {
        boolean roomHasActiveSession =
                sessionRepository.findAll()
                        .stream()
                        .filter(session ->
                                session.getRoom()
                                        .getId()
                                        .equals(room.getId())
                        )
                        .anyMatch(session ->
                                session.getEndTime() != null
                                        && session.getEndTime()
                                        .isAfter(startTime)
                        );

        if (roomHasActiveSession) {
            throw new IllegalStateException(
                    "Room with id: "
                            + room.getId()
                            + " has an active session"
            );
        }
    }

    private void chargeClient(
            Client client,
            BigDecimal totalCost
    ) {
        BigDecimal availableBudget =
                client.getMonthlyBudget();

        if (availableBudget == null) {
            availableBudget = BigDecimal.ZERO;
        }

        if (availableBudget.compareTo(totalCost) >= 0) {
            client.setMonthlyBudget(
                    availableBudget.subtract(totalCost)
            );

            return;
        }

        if (!canUseCredit(client)) {
            throw new IllegalStateException(
                    "Client with id: "
                            + client.getId()
                            + " does not have enough money"
            );
        }

        BigDecimal missingAmount =
                totalCost.subtract(availableBudget);

        BigDecimal currentDebt = client.getDebt();

        if (currentDebt == null) {
            currentDebt = BigDecimal.ZERO;
        }

        client.setMonthlyBudget(BigDecimal.ZERO);
        client.setDebt(
                currentDebt.add(missingAmount)
        );

        BigDecimal criticalDebt =
                client.getSalary()
                        .multiply(new BigDecimal("3.00"));

        if (client.getDebt()
                .compareTo(criticalDebt) >= 0) {

            client.setActive(false);
        }
    }

    private boolean canUseCredit(Client client) {
        return client.getSocialStatus()
                == SocialStatus.VIP
                || client.getSocialStatus()
                == SocialStatus.POLITICIAN;
    }

    private void infectClientIfNecessary(
            Client client,
            Worker worker,
            Session session
    ) {
        if (!worker.isInfected()) {
            session.setInfected(false);
            return;
        }

        session.setInfected(true);

        client.setInfectedUntil(
                LocalDateTime.now()
                        .plusDays(INFECTION_DURATION_DAYS)
        );

        client.setActive(false);
    }

    private int calculateDurationMinutes(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        long minutes = Duration.between(
                startTime,
                endTime
        ).toMinutes();

        if (minutes < 1) {
            return 1;
        }

        if (minutes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) minutes;
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

    private Worker findWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() ->
                        new WorkerNotFoundException(
                                "Worker with id: "
                                        + id
                                        + " not found"
                        )
                );
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

    private Session findSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new SessionNotFoundException(
                                "Session with id: "
                                        + id
                                        + " not found"
                        )
                );
    }
}