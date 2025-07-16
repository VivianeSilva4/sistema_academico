package com.example.sistema_academico.dto.update;

import jakarta.validation.constraints.NotBlank;

public record UpdateCampusDto(@NotBlank String nome,@NotBlank String endereco) {
}
