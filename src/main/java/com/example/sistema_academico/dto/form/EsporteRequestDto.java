package com.example.sistema_academico.dto.form;

import jakarta.validation.constraints.*;


public record EsporteRequestDto(@NotBlank String nome,
                                @NotNull @Min(1) int minimoDeAtleta,
                                @NotNull @Min(1) int maximoDeAtleta) {
}
