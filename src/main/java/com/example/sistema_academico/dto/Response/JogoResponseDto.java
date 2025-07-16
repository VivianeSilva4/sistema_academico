package com.example.sistema_academico.dto.Response;


import com.example.sistema_academico.model.role.Fase;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;


public record JogoResponseDto(Integer id,
                              Integer placaA,
                              Integer placaB,
                              LocalDateTime dataHora,
                              boolean woA,
                              boolean woB,
                              Fase fase,
                              String arbitro,
                              String equipeA,
                              String equipeB,
                              String grupo) {
}
