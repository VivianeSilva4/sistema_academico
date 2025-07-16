package com.example.sistema_academico.dto.Response;

import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.model.Campus;
import com.example.sistema_academico.model.role.Grau;

public record CursoResponseDto(Integer id,
                               String nome,
                               Grau nivel,
                               CampusResponseDto campus) {
}
