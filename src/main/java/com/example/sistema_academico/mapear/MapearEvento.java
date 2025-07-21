package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.EventoResponseDto;
import com.example.sistema_academico.dto.form.EventoRequestDto;
import com.example.sistema_academico.model.Esportes;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.repository.IEsporteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MapearEvento {
    @Autowired
     private IEsporteRepository esporteRepository;

    public Eventos toEntity(EventoRequestDto evento, Esportes esporte){

        Date dataEvento = Date.from(ZonedDateTime.now(
                ZoneId.of("America/Sao_Paulo")).toInstant());

        return new Eventos(
                null,
                evento.nome(),
                dataEvento,
                evento.nivel(),
                esporte,
                null,
                null);
    }

    public EventoResponseDto toDto(Eventos evento){
        return new EventoResponseDto(
                evento.getId(),
                evento.getNome(),
                evento.getData(),
                evento.getNivel(),
                evento.getEsporte().getNome());
    }
}
