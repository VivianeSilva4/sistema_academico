package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.GrupoResponseDto;
import com.example.sistema_academico.mapear.MapearGrupo;
import com.example.sistema_academico.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v8/api")
public class GrupoController {
    private final GrupoService grupoService;

    @PostMapping("/gerarGrupos/{id}")
    public ResponseEntity<GrupoResponseDto> gerarGrupo(@PathVariable Integer id){
         grupoService.gerarGrupo(id);
         return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{grupoId}")
    public ResponseEntity<GrupoResponseDto> getById(@PathVariable("grupoId") Integer id){
        var grupo = grupoService.buscarGrupo(id);
        if(grupo.isPresent()){
            MapearGrupo mapearGrupo = new MapearGrupo();
            GrupoResponseDto responseDto = mapearGrupo.toDto(grupo.get());
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<GrupoResponseDto>> listaGrupo(){
        MapearGrupo mapearGrupo = new MapearGrupo();
        var grupo = grupoService.listarGrupo();
        List<GrupoResponseDto> dtos = grupo.stream().map(mapearGrupo::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{grupoId}")
    public ResponseEntity<Void> apagarGrupo(@PathVariable("grupoId") Integer grupoId){
        grupoService.deletarGrupo(grupoId);
        return ResponseEntity.noContent().build();
    }

}
