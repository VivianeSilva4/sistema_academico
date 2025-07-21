package com.example.sistema_academico.dto.Response;

import com.example.sistema_academico.domain.Grau;

public record CursoResponseDto(Integer id,
                               String nome,
                               Grau nivel,
                               CampusResponseDto campus) {
}
