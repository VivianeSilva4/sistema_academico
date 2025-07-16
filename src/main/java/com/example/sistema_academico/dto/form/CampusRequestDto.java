package com.example.sistema_academico.dto.form;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CampusRequestDto(@NotBlank String nome,
                               @NotBlank String endereco) {
}
