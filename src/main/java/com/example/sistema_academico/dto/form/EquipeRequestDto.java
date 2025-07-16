package com.example.sistema_academico.dto.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EquipeRequestDto(
                              @NotBlank String nome,
                              @NotNull Integer tecnicoDaEquipe,
                              @NotNull Integer esporte,
                              @NotNull Integer curso,
                              List<Integer> atletas
                        ) {

}
