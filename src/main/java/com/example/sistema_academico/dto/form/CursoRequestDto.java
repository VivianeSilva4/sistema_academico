package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.domain.Grau;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CursoRequestDto(@NotBlank String nome,
                              @NotNull Grau nivel,
                              @NotNull Integer campus) {
}
