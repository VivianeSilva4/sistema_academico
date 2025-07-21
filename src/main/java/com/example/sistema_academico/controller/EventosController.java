package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.EventoResponseDto;
import com.example.sistema_academico.dto.form.EventoRequestDto;
import com.example.sistema_academico.mapear.MapearEvento;
import com.example.sistema_academico.service.EventoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v6/api")
public class EventosController {
    private final EventoService eventoService;

    @PostMapping("/eventos")
    public ResponseEntity<EventoResponseDto> salvarEvento(@Valid @RequestBody EventoRequestDto eventoDto) {
        try {
            var evento = eventoService.salvarEvento(eventoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{eventoId}")
    public ResponseEntity<EventoResponseDto> getById(@PathVariable("eventoId") Integer id) {
        try {
            MapearEvento mapearEvento = new MapearEvento();
            var evento = eventoService.buscarEvento(id);
            if (evento.isPresent()) {
                EventoResponseDto responseDto = mapearEvento.toDto(evento.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<EventoResponseDto>> ListaEvento() {
        try {
            MapearEvento mapearEvento = new MapearEvento();
            var evento = eventoService.listarEvento();
            List<EventoResponseDto> dtos = evento.stream()
                    .map(mapearEvento::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{eventoId}")
    public ResponseEntity<Void> apagarEvento(@PathVariable("eventoId") Integer eventoId) {
        try {
            eventoService.deletarEvento(eventoId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

