package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.model.role.Grau;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.TimeZone;

public record EventoRequestDto(@NotBlank String nome,
                                TimeZone data,
                               @NotNull Grau nivel,
                               @NotNull @Min(1) Integer esporte) {
}
