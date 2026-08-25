package com.example.aferapodkarpacka.model.worker;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Min(18)
    private int age;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal hourlyRate;

    private boolean infected;
}