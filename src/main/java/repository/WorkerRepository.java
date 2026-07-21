package repository;


import model.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findByInfectedTrue();

    List<Worker> findByInfectedFalse();

    List<Worker> findByAvailableTrue();

    List<Worker> findByAvailableTrueAndInfectedFalse();

    List<Worker> findByAvailableTrueAndInfectedTrue();}
