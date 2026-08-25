package com.example.aferapodkarpacka.model.client;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.aferapodkarpacka.model.enums.PoliticalParty;
import com.example.aferapodkarpacka.model.enums.SocialStatus;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Min(18)
    private int age;

    @NotNull
    private SocialStatus socialStatus;

    private PoliticalParty party;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal monthlyBudget;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal debt;

    private boolean active;
}