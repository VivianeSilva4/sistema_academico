package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.domain.Fase;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record JogoRequestDto(
        @NotNull Integer placaA,
        @NotNull Integer placaB,
        @NotNull LocalDateTime dataHora,
        @NotNull boolean woA,
        @NotNull boolean woB,
        boolean finalizado,
        @NotNull Fase fase,
        @NotNull Integer arbitro,
        @NotNull Integer equipeA,
        @NotNull Integer equipeB,
        @NotNull Integer grupo,
        @NotNull Integer evento) {
}
