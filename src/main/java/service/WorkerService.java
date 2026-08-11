package service;

import exceptions.WorkerNotFoundException;
import lombok.RequiredArgsConstructor;
import model.worker.Worker;
import model.worker.WorkerMapper;
import model.worker.WorkerRequest;
import model.worker.WorkerResponse;
import org.springframework.stereotype.Service;
import repository.WorkerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;

    public WorkerResponse createWorker(WorkerRequest request) {
        Worker worker = workerMapper.toEntity(request);
        Worker savedMyWorker = workerRepository.save(worker);
        return workerMapper.toResponse(savedMyWorker);
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

    public WorkerResponse updateMyWorker(Long id, WorkerRequest request) {
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