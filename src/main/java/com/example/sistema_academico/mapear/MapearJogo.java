package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
import com.example.sistema_academico.dto.form.JogoRequestDto;
import com.example.sistema_academico.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MapearJogo {

    public static Jogo toEntity(JogoRequestDto jogoDto, Usuario arbitro,
                                Grupo grupo, Equipes equipeA, Equipes equipeB, Eventos evento){

        return new Jogo(null,
                jogoDto.placaA(),
                jogoDto.placaB(),
                LocalDateTime.now(),
                jogoDto.woA(),
                jogoDto.woB(),
                jogoDto.finalizado(),
                jogoDto.fase(),
                arbitro,
                equipeA,
                equipeB,
                evento,
                grupo);
    }
    public static JogoResponseDto toDto(Jogo jogo){
        return new JogoResponseDto(
                jogo.getId(),
                jogo.getPlacaA(),
                jogo.getPlacaB(),
                jogo.getDataHora(),
                jogo.getWoA(),
                jogo.getWoB(),
                jogo.isFinalizado(),
                jogo.getFase(),
                jogo.getArbitro().getNomeCompleto(),
                jogo.getEquipeA().getNome(),
                jogo.getEquipeB().getNome(),
                jogo.getGrupo().getNome()
        );
    }
}
