package repository;


import model.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);

    boolean existsByName(String name);

    List<Room> findByOccupiedFalse();

    List<Room> findByOccupiedTrue();

    List<Room> findByMonitoredTrue();

    List<Room> findByMonitoredFalse();

    List<Room> findByOccupiedFalseAndMonitoredFalse();

    List<Room> findByOccupiedFalseAndMonitoredTrue();}