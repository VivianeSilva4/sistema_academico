package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.EquipeResponseDto;
import com.example.sistema_academico.dto.form.EquipeRequestDto;
import com.example.sistema_academico.mapear.MapearEquipe;
import com.example.sistema_academico.service.EquipesService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v7/api")
public class EquipesController {
    private final EquipesService equipesService;

    @PostMapping("/equipes")
    public ResponseEntity<EquipeResponseDto> criarEquipes(@Valid @RequestBody EquipeRequestDto equipeDto) {
        try {
            var equipe = equipesService.criarEquipe(equipeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(equipe);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{equipeId}")
    public ResponseEntity<EquipeResponseDto> getById(@PathVariable("equipeId") Integer id) {
        try {
            var equipe = equipesService.buscarEquipes(id);
            if (equipe.isPresent()) {
                var responseDto = MapearEquipe.toDto(equipe.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<EquipeResponseDto>> ListarJogo() {
        try {
            var equipe = equipesService.listarEquipes();
            List<EquipeResponseDto> dtos = equipe.stream()
                    .map(MapearEquipe::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{equipeId}")
    public ResponseEntity<Void> deletarJogo(@PathVariable("equipeId") Integer equipeId) {
        try {
            equipesService.deletarEquipe(equipeId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

