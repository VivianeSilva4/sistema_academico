package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.EsporteResponseDto;
import com.example.sistema_academico.dto.form.EsporteRequestDto;
import com.example.sistema_academico.model.Esportes;

public class MapearEsporte {

    public static Esportes toEntity(EsporteRequestDto esporte){
        return new Esportes(
                null,
                esporte.nome(),
                esporte.minimoDeAtleta(),
                esporte.maximoDeAtleta(),
                null,
                null);
    }

    public static EsporteResponseDto toDto(Esportes esportes){
        return new EsporteResponseDto(
                esportes.getId(),
                esportes.getNome(),
                esportes.getMinimoDeAtletas(),
                esportes.getMaximoDeAtletas());
    }
}


