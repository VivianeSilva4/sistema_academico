package com.example.sistema_academico.dto.update;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateJogoDto( Integer placaA,
                             Integer placaB,
                             LocalDateTime dataHora,
                             boolean woA,
                             boolean woB,
                             boolean finalizado) {
}
