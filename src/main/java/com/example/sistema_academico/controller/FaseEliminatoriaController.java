package com.example.sistema_academico.controller;


import com.example.sistema_academico.service.FaseEliminatoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v9/api")
@RequiredArgsConstructor
public class FaseEliminatoriaController {
    private final FaseEliminatoriaService faseEliminatoriaService;

    @PostMapping("/gerar/{eventoId}")
    public ResponseEntity<String> gerarFaseEliminatoria(@PathVariable Integer eventoId) {
        faseEliminatoriaService.gerarFaseEliminatoria(eventoId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Fase eliminatória gerada com sucesso para o evento ID: " + eventoId);
    }

}
