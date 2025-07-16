package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
import com.example.sistema_academico.dto.form.JogoRequestDto;
import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Grupo;
import com.example.sistema_academico.model.Jogo;
import com.example.sistema_academico.model.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MapearJogo {

    public static Jogo toEntity(JogoRequestDto jogo, Usuario arbitro,
                                Grupo grupo, Equipes equipeA, Equipes equipeB){


        return new Jogo(null,
                jogo.placaA(),
                jogo.placaB(),
                LocalDateTime.now(),
                jogo.woA(),
                jogo.woB(),
                jogo.fase(),
                arbitro,
                equipeA,
                equipeB,
                null,
                grupo,
                null);
    }
    public static JogoResponseDto toDto(Jogo jogo){
        return new JogoResponseDto(
                jogo.getId(),
                jogo.getPlacaA(),
                jogo.getPlacaB(),
                jogo.getDataHora(),
                jogo.getWoA(),
                jogo.getWoB(),
                jogo.getFase(),
                jogo.getArbitro().getNomeCompleto(),
                jogo.getEquipeA().getNome(),
                jogo.getEquipeB().getNome(),
                jogo.getGrupo().getNome()
        );
    }
}
