package com.example.aferapodkarpacka.model.session;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.aferapodkarpacka.model.client.Client;
import com.example.aferapodkarpacka.model.room.Room;
import com.example.aferapodkarpacka.model.worker.Worker;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationMinutes;

    @Column(scale = 2)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private boolean infected;
}