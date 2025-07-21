package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.CursoResponseDto;
import com.example.sistema_academico.dto.form.CursoRequestDto;
import com.example.sistema_academico.mapear.MapearCurso;
import com.example.sistema_academico.dto.update.UpdateCursoDto;
import com.example.sistema_academico.service.CursoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v3/api")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @PostMapping("/cursos")
    public ResponseEntity<CursoResponseDto> salvarCurso(@Valid @RequestBody CursoRequestDto cursoDto) {
        try {
            var curso = cursoService.salvarCurso(cursoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(curso);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<CursoResponseDto> getById(@PathVariable("cursoId") Integer id) {
        try {
            var curso = cursoService.buscarCurso(id);
            if (curso.isPresent()) {
                CursoResponseDto responseDto = MapearCurso.toDto(curso.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<CursoResponseDto>> ListaCurso() {
        try {
            var curso = cursoService.listarCurso();
            List<CursoResponseDto> dtos = curso.stream().map(MapearCurso::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{cursoId}")
    public ResponseEntity<Void> apagarCampus(@PathVariable("cursoId") Integer cursoId) {
        try {
            cursoService.deletarCurso(cursoId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{cursoId}")
    public ResponseEntity<Void> atualizarCampus(@PathVariable("cursoId") Integer id,
                                                @RequestBody UpdateCursoDto cursoDto) {
        try {
            cursoService.atualizarDados(id, cursoDto);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
