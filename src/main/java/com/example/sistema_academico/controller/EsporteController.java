package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.EsporteResponseDto;
import com.example.sistema_academico.dto.form.EsporteRequestDto;
import com.example.sistema_academico.mapear.MapearEsporte;
import com.example.sistema_academico.service.EsporteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v5/api")
public class EsporteController {

    private final EsporteService esporteService;

    @PostMapping("/esportes")
    public ResponseEntity<EsporteResponseDto> salvarEsporte(@Valid @RequestBody EsporteRequestDto esporteDto) {
        try {
            var esporte = esporteService.salvarEsporte(esporteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(esporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{esporteId}")
    public ResponseEntity<EsporteResponseDto> getById(@PathVariable("esporteId") Integer id) {
        try {
            var esporte = esporteService.buscarEsporte(id);
            if (esporte.isPresent()) {
                EsporteResponseDto responseDto = MapearEsporte.toDto(esporte.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<EsporteResponseDto>> ListaEsporte() {
        try {
            var esporte = esporteService.listarEsporte();
            List<EsporteResponseDto> dtos = esporte.stream().map(MapearEsporte::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{esporteId}")
    public ResponseEntity<Void> apagarEsporte(@PathVariable("esporteId") Integer esporteId) {
        try {
            esporteService.deletarEsporte(esporteId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

