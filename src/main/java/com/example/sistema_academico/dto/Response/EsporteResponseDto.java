package com.example.sistema_academico.dto.Response;

public record EsporteResponseDto(Integer id,
                                 String nome,
                                 int minimoDeAtleta,
                                 int maximoDeAtleta) {
}
