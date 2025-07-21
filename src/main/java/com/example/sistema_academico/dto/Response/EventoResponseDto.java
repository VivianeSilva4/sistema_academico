package com.example.sistema_academico.dto.Response;

import com.example.sistema_academico.domain.Grau;

import java.util.Date;

public record EventoResponseDto(Integer id,
                                String nome,
                                Date data,
                                Grau nivel,
                                String esporte) {
}
