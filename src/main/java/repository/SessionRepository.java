package repository;


import model.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByClientId(Long clientId);

    List<Session> findByWorkerId(Long workerId);

    List<Session> findByRoomId(Long roomId);

    List<Session> findByStartTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Session> findByWorkerIdAndStartTimeBetween(
            Long workerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Session> findByClientIdAndStartTimeBetween(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Session> findByInfectedTrue();

    boolean existsByRoomIdAndEndTimeAfter(
            Long roomId,
            LocalDateTime dateTime
    );

    boolean existsByWorkerIdAndEndTimeAfter(
            Long workerId,
            LocalDateTime dateTime
    );

}