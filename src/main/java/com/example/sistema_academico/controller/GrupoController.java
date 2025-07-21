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
    public ResponseEntity<?> gerarGrupo(@PathVariable Integer id){
        try {
            grupoService.gerarGrupos(id);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar grupos: " + e.getMessage());
        }
    }

    @GetMapping("/{grupoId}")
    public ResponseEntity<?> getById(@PathVariable("grupoId") Integer id){
        try {
            var grupo = grupoService.buscarGrupo(id);
            if(grupo.isPresent()){
                MapearGrupo mapearGrupo = new MapearGrupo();
                GrupoResponseDto responseDto = mapearGrupo.toDto(grupo.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar grupo: " + e.getMessage());
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<?> listaGrupo(){
        try {
            MapearGrupo mapearGrupo = new MapearGrupo();
            var grupo = grupoService.listarGrupo();
            List<GrupoResponseDto> dtos = grupo.stream().map(mapearGrupo::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar grupos: " + e.getMessage());
        }
    }

    @DeleteMapping("/{grupoId}")
    public ResponseEntity<?> apagarGrupo(@PathVariable("grupoId") Integer grupoId){
        try {
            grupoService.deletarGrupo(grupoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar grupo: " + e.getMessage());
        }
    }
}

