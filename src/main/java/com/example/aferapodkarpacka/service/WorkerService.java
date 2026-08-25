package com.example.aferapodkarpacka.service;

import com.example.aferapodkarpacka.exceptions.WorkerNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.aferapodkarpacka.model.worker.Worker;
import com.example.aferapodkarpacka.model.worker.WorkerMapper;
import com.example.aferapodkarpacka.model.worker.WorkerRequest;
import com.example.aferapodkarpacka.model.worker.WorkerResponse;
import org.springframework.stereotype.Service;
import com.example.aferapodkarpacka.repository.WorkerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;

    public WorkerResponse createWorker(WorkerRequest request) {
        Worker worker = workerMapper.toEntity(request);
        Worker savedWorker = workerRepository.save(worker);
        return workerMapper.toResponse(savedWorker);
    }

    public WorkerResponse getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("not found"));
        return workerMapper.toResponse(worker);
    }

    public List<WorkerResponse> getAllWorkers() {
        return workerRepository.findAll()
                .stream()
                .map(workerMapper::toResponse)
                .toList();
    }

    public WorkerResponse updateWorker(Long id, WorkerRequest request) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("not found"));

        workerMapper.updateEntity(request, worker);
        Worker savedWorker = workerRepository.save(worker);
        return workerMapper.toResponse(savedWorker);
    }

    public void deleteWorker(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("not found"));

        workerRepository.delete(worker);
    }
}