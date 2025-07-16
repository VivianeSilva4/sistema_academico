package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.EsporteResponseDto;
import com.example.sistema_academico.dto.form.EsporteRequestDto;
import com.example.sistema_academico.mapear.MapearEsporte;
import com.example.sistema_academico.service.EsporteService;
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
    public ResponseEntity<EsporteResponseDto> salvarEsporte(@Valid @RequestBody EsporteRequestDto esporteDto){
        var esporte = esporteService.salvarEsporte(esporteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(esporte);
    }

    @GetMapping("/{esporteId}")
    public ResponseEntity<EsporteResponseDto> getById(@PathVariable("esporteId") Integer id){
        var esporte = esporteService.buscarEsporte(id);
        if(esporte.isPresent()){
            EsporteResponseDto responseDto = MapearEsporte.toDto(esporte.get());
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<EsporteResponseDto>> ListaEsporte(){
        var esporte = esporteService.listarEsporte();
        List<EsporteResponseDto> dtos = esporte.stream().map(MapearEsporte::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{esporteId}")
    public ResponseEntity<Void> apagarEsporte(@PathVariable("esporteId") Integer esporteId){
        esporteService.deletarEsporte(esporteId);
        return ResponseEntity.noContent().build();
    }

}
