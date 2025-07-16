package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.EventoResponseDto;
import com.example.sistema_academico.dto.form.EventoRequestDto;
import com.example.sistema_academico.mapear.MapearEvento;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.repository.IEsporteRepository;
import com.example.sistema_academico.repository.IEventosRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventoService {
    private final IEventosRepository eventoRepository;
    private final IEsporteRepository esporteRepository;

    @Transactional
    public EventoResponseDto salvarEvento(EventoRequestDto eventoDto){
        var esporte = esporteRepository.findById(eventoDto.esporte())
                .orElseThrow(() -> new EntityNotFoundException("Esse esporte não existe"));

        MapearEvento mapearEvento = new MapearEvento();
        var evento = eventoRepository.save(mapearEvento.toEntity(eventoDto,esporte));
        return mapearEvento.toDto(evento);
    }
    @Transactional(readOnly = true)
    public Optional<Eventos> buscarEvento(Integer id){
        return eventoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Eventos> listarEvento(){
        return eventoRepository.findAll();
    }

    @Transactional
    public void deletarEvento(Integer id){
        var eventoExist =  eventoRepository.existsById(id);

        if(eventoExist){
            eventoRepository.deleteById(id);
        }

    }
}
