package com.example.aferapodkarpacka.service;

import com.example.aferapodkarpacka.exceptions.WorkerNotFoundException;
import com.example.aferapodkarpacka.model.worker.Worker;
import com.example.aferapodkarpacka.model.worker.WorkerMapper;
import com.example.aferapodkarpacka.model.worker.WorkerRequest;
import com.example.aferapodkarpacka.model.worker.WorkerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.aferapodkarpacka.repository.WorkerRepository;
import com.example.aferapodkarpacka.service.WorkerService;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTests {

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @InjectMocks
    private WorkerService workerService;

    private Worker worker;
    private WorkerRequest request;
    private WorkerResponse response;

    @BeforeEach
    void setUp() {
        worker = new Worker();
        worker.setId(1L);
        request = new WorkerRequest();
        response = new WorkerResponse();
        response.setId(1L);
    }

    @Test
    void shouldCreateWorkerSuccessfully() {
        when(workerMapper.toEntity(request)).thenReturn(worker);
        when(workerRepository.save(worker)).thenReturn(worker);
        when(workerMapper.toResponse(worker)).thenReturn(response);

        WorkerResponse result = workerService.createWorker(request);

        assertThat(result).isNotNull();
        verify(workerRepository).save(worker);
    }

    @Test
    void shouldGetWorkerById() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(workerMapper.toResponse(worker)).thenReturn(response);

        WorkerResponse result = workerService.getWorkerById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowWorkerNotFoundExceptionWhenWorkerNotFound() {
        when(workerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workerService.getWorkerById(1L))
                .isInstanceOf(WorkerNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldGetAllWorkers() {
        when(workerRepository.findAll()).thenReturn(List.of(worker));
        when(workerMapper.toResponse(worker)).thenReturn(response);

        List<WorkerResponse> result = workerService.getAllWorkers();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldUpdateWorkerSuccessfully() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(workerRepository.save(worker)).thenReturn(worker);
        when(workerMapper.toResponse(worker)).thenReturn(response);

        WorkerResponse result = workerService.updateWorker(1L, request);

        assertThat(result).isNotNull();
        verify(workerMapper).updateEntity(request, worker);
    }

    @Test
    void shouldThrowWorkerNotFoundExceptionOnUpdateWhenWorkerNotFound() {
        when(workerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workerService.updateWorker(1L, request))
                .isInstanceOf(WorkerNotFoundException.class)
                .hasMessage("not found");
    }

    @Test
    void shouldDeleteWorkerSuccessfully() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));

        workerService.deleteWorker(1L);

        verify(workerRepository).delete(worker);
    }

    @Test
    void shouldThrowWorkerNotFoundExceptionOnDeleteWhenWorkerNotFound() {
        when(workerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workerService.deleteWorker(1L))
                .isInstanceOf(WorkerNotFoundException.class)
                .hasMessage("not found");
    }
}