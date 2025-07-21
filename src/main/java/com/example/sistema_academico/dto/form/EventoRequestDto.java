package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.domain.Grau;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.TimeZone;

public record EventoRequestDto(@NotBlank String nome,
                                TimeZone data,
                               @NotNull Grau nivel,
                               @NotNull Integer esporte) {
}
