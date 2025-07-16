package com.example.sistema_academico.dto.Response;

import com.example.sistema_academico.model.role.Grau;

import java.security.Timestamp;
import java.util.Date;
import java.util.TimeZone;

public record EventoResponseDto(Integer id,
                                String nome,
                                Date data,
                                Grau nivel,
                                String esporte) {
}
