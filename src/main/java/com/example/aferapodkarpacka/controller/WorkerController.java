package com.example.aferapodkarpacka.controller;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.example.aferapodkarpacka.model.worker.WorkerRequest;
import com.example.aferapodkarpacka.model.worker.WorkerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.aferapodkarpacka.service.WorkerService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    public ResponseEntity<List<WorkerResponse>> getAllWorkers() {
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponse> getWorkerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(workerService.getWorkerById(id));
    }

    @PostMapping
    public ResponseEntity<WorkerResponse> createWorker(
            @Valid
            @NotNull
            @RequestBody WorkerRequest request) {

        return ResponseEntity
                .status(201)
                .body(workerService.createWorker(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerResponse> updateWorker(
            @PathVariable Long id,
            @Valid
            @NotNull
            @RequestBody WorkerRequest request) {

        return ResponseEntity.ok(
                workerService.updateWorker(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(
            @PathVariable Long id) {

        workerService.deleteWorker(id);

        return ResponseEntity.noContent().build();
    }
}