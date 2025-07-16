package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.CampusResponseDto;
import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.model.Campus;

public class MapearCampus {

    public static Campus toEntity(CampusRequestDto dto){
        return new Campus(
                null,
                dto.nome(),
                dto.endereco(),
                null);
    }

    public static CampusResponseDto toDto(Campus campus){
        return new CampusResponseDto(
                campus.getId(),
                campus.getNome(),
                campus.getEndereco()
        );
    }

}
