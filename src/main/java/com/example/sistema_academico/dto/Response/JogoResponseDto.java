package com.example.sistema_academico.dto.Response;


import com.example.sistema_academico.domain.Fase;

import java.time.LocalDateTime;


public record JogoResponseDto(Integer id,
                              Integer placaA,
                              Integer placaB,
                              LocalDateTime dataHora,
                              boolean woA,
                              boolean woB,
                              boolean finalizado,
                              Fase fase,
                              String arbitro,
                              String equipeA,
                              String equipeB,
                              String grupo) {
}
