package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.CampusResponseDto;
import com.example.sistema_academico.dto.Response.CursoResponseDto;
import com.example.sistema_academico.dto.form.CursoRequestDto;
import com.example.sistema_academico.model.Campus;
import com.example.sistema_academico.model.Cursos;
import com.example.sistema_academico.repository.ICampusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;


public class MapearCurso {
    @Autowired
    private ICampusRepository campusRepository;

    public  Cursos toEntity(CursoRequestDto dto){
        Campus campus = campusRepository.findById(dto.campus())
                .orElseThrow(() -> new EntityNotFoundException("Campus não encontrado"));

            return new Cursos(
                    null,
                    dto.nome(),
                    dto.nivel(),
                    campus,
                    null,
                    null);

    }
    public static CursoResponseDto toDto(Cursos curso){
        return new CursoResponseDto(
                curso.getId(),
                curso.getNome(),
                curso.getNivel(),
                new CampusResponseDto(
                        curso.getCampus().getId(),
                        curso.getCampus().getNome(),
                        curso.getCampus().getEndereco()
                )
        );
    }
}
