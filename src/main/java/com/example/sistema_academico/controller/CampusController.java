package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.CampusResponseDto;
import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.mapear.MapearCampus;
import com.example.sistema_academico.dto.update.UpdateCampusDto;
import com.example.sistema_academico.service.CampusService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api")
public class CampusController {

    private final CampusService campusService;

    @PostMapping("/campus")
    public ResponseEntity<?> salvarCampus(@Valid @RequestBody CampusRequestDto campus) {
        try {
            campusService.salvar(campus);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro ao salvar campus: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao salvar campus.");
        }
    }

    @GetMapping("/{campusId}")
    public ResponseEntity<?> getById(@PathVariable("campusId") Integer id) {
        try {
            var campus = campusService.buscarCampus(id);
            if (campus.isPresent()) {
                var dto = MapearCampus.toDto(campus.get());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Campus não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar campus.");
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<?> ListaCampus() {
        try {
            var campus = campusService.listarCampus();
            List<CampusResponseDto> dto = campus.stream().map(MapearCampus::toDto).toList();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar campi.");
        }
    }

    @DeleteMapping("/{campusId}")
    public ResponseEntity<?> apagarCampus(@PathVariable("campusId") Integer userId) {
        try {
            campusService.deletarCampus(userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Campus não encontrado para exclusão.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao apagar campus.");
        }
    }

    @PutMapping("/{campusId}")
    public ResponseEntity<?> atualizarCampus(@PathVariable("campusId") Integer id,
                                             @RequestBody UpdateCampusDto campusDto) {
        try {
            campusService.atualizarDados(id, campusDto);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Campus não encontrado para atualização.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar campus.");
        }
    }
}
