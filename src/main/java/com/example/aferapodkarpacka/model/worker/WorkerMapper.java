package com.example.aferapodkarpacka.model.worker;

import org.springframework.stereotype.Component;

@Component
public class WorkerMapper {

    public Worker toEntity(WorkerRequest request) {
        if (request == null) return null;

        Worker worker = new Worker();
        worker.setFirstName(request.getFirstName());
        worker.setLastName(request.getLastName());
        worker.setAge(request.getAge());
        worker.setHourlyRate(request.getHourlyRate());
        worker.setEarningsTotal(java.math.BigDecimal.ZERO);

        worker.setInfected(request.isInfected());

        return worker;
    }

    public void updateEntity(WorkerRequest request, Worker worker) {
        if (request == null || worker == null) return;

        worker.setFirstName(request.getFirstName());
        worker.setLastName(request.getLastName());
        worker.setAge(request.getAge());
        worker.setHourlyRate(request.getHourlyRate());
        worker.setInfected(request.isInfected());
    }

    public WorkerResponse toResponse(Worker worker) {
        if (worker == null) return null;

        WorkerResponse response = new WorkerResponse();
        response.setId(worker.getId());
        response.setFirstName(worker.getFirstName());
        response.setLastName(worker.getLastName());
        response.setAge(worker.getAge());
        response.setHourlyRate(worker.getHourlyRate());
        response.setEarningsTotal(worker.getEarningsTotal());
        response.setInfected(worker.isInfected());

        return response;
    }
}