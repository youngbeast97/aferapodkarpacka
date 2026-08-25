package com.example.aferapodkarpacka.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.aferapodkarpacka.model.enums.PoliticalParty;
import com.example.aferapodkarpacka.model.enums.SocialStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private int age;

    private SocialStatus socialStatus;
    private PoliticalParty party;

    private BigDecimal monthlyBudget;
    private BigDecimal salary;
    private BigDecimal debt;

    private boolean active;
    private LocalDateTime infectedUntil;
}