package controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import model.session.SessionRequest;
import model.session.SessionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.SessionService;


@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSessionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                sessionService.getSessionById(id)
        );
    }

    @PostMapping
    public ResponseEntity<SessionResponse> startSession(
            @Valid
            @NotNull
            @RequestBody SessionRequest request) {

        return ResponseEntity
                .status(201)
                .body(sessionService.startSession(request));
    }
}