package com.example.sistema_academico.controller;

import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.service.FaseEliminatoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v9/api")
@RequiredArgsConstructor
public class FaseEliminatoriaController {

    private final FaseEliminatoriaService faseEliminatoriaService;

    @PostMapping("/gerarFaseEliminatoria/{eventoId}")
    public ResponseEntity<?> gerarFaseEliminatoria(@PathVariable Integer eventoId) {
        try {
            faseEliminatoriaService.gerarFaseEliminatoria(eventoId);
            return ResponseEntity.ok("Fase eliminatória gerada com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao gerar fase eliminatória: " + e.getMessage());
        }
    }


    @PostMapping("/semifinal/chave-cheia/{eventoId}")
    public ResponseEntity<?> gerarSemifinalChaveCheia(@PathVariable Integer eventoId) {
        try {
            faseEliminatoriaService.gerarSemifinalChaveCheia(eventoId);
            return ResponseEntity.ok("Semifinais (chave cheia) geradas com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao gerar semifinais (chave cheia): " + e.getMessage());
        }
    }

    @PostMapping("/semifinal/chave-nao-cheia/{eventoId}")
    public ResponseEntity<?> gerarSemifinalChaveNaoCheia(@PathVariable Integer eventoId) {
        try {
            faseEliminatoriaService.gerarSemifinalChaveNaoCheia(eventoId);
            return ResponseEntity.ok("Semifinais (chave não cheia) geradas com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao gerar semifinais (chave não cheia): " + e.getMessage());
        }
    }

    @PostMapping("/final/{eventoId}")
    public ResponseEntity<?> gerarFinal(@PathVariable Integer eventoId) {
        try {
            faseEliminatoriaService.gerarFinal(eventoId);
            return ResponseEntity.ok("Final gerada com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao gerar a final: " + e.getMessage());
        }
    }
    @GetMapping("/vencedor-torneio/{eventoId}")
    public ResponseEntity<?> obterVencedorDoTorneio(@PathVariable Integer eventoId) {
        try {
            Equipes vencedor = faseEliminatoriaService.obterVencedorDoTorneio(eventoId);
            return ResponseEntity.ok("Vencedor do torneio: " + vencedor.getNome());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao obter o vencedor do torneio: " + e.getMessage());
        }
    }
}