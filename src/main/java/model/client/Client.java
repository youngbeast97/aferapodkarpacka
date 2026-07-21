package model.client;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.PoliticalParty;
import model.enums.SocialStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialStatus socialStatus;

    @Enumerated(EnumType.STRING)
    private PoliticalParty party;

    @Column(nullable = false)
    private BigDecimal monthlyBudget;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private BigDecimal debt;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime infectedUntil;
}