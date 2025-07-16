package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.EquipeResponseDto;
import com.example.sistema_academico.dto.form.EquipeRequestDto;
import com.example.sistema_academico.model.Cursos;
import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Esportes;
import com.example.sistema_academico.model.Usuario;

import java.util.List;

public class MapearEquipe {

    public static Equipes toEntity(EquipeRequestDto equipe, Usuario tecnicoDaEquipe,
                                   Esportes esporte, Cursos curso){
        return new Equipes(
                    null,
                    equipe.nome(),
                    tecnicoDaEquipe,
                    null,
                    null,
                    null,
                    null,
                    esporte,curso,
                    null
        );
    }
    public static EquipeResponseDto toDto(Equipes equipe){
        return new EquipeResponseDto(
                equipe.getId(),
                equipe.getNome(),
                equipe.getTecnicoDaEquipe().getNomeCompleto(),
                equipe.getEsporte().getNome(),
                equipe.getCurso().getNome());
    }
}
