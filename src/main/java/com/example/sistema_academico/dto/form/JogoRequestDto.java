package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.model.role.Fase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.TimeZone;


public record JogoRequestDto(
        @NotNull Integer placaA,
        @NotNull Integer placaB,
        @NotNull LocalDateTime dataHora,
        @NotNull boolean woA,
        @NotNull boolean woB,
        @NotNull Fase fase,
        @NotNull Integer arbitro,
        @NotNull Integer equipeA,
        @NotNull Integer equipeB,
        @NotNull Integer grupo) {
}
