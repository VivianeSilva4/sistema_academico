package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
import com.example.sistema_academico.dto.form.GerarJogosDto;
import com.example.sistema_academico.dto.form.JogoRequestDto;
import com.example.sistema_academico.dto.update.UpdateJogoDto;
import com.example.sistema_academico.mapear.MapearJogo;
import com.example.sistema_academico.model.Jogo;
import com.example.sistema_academico.service.JogoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v4/api")
public class JogoController {

    private final JogoService jogoService;

    @PostMapping("/jogos")
    public ResponseEntity<?> criarJogos(@Valid @RequestBody JogoRequestDto jogoDto){
        try {
            var jogo = jogoService.criarJogos(jogoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(jogo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar jogo: " + e.getMessage());
        }
    }

    @PostMapping("/jogos/gerar")
    public ResponseEntity<?> gerarJogos(@RequestBody GerarJogosDto gerarJogosDto) {
        try {
            jogoService.gerarJogos(gerarJogosDto);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar jogos: " + e.getMessage());
        }
    }

    @GetMapping("/{jogoId}")
    public ResponseEntity<?> getById(@PathVariable("jogoId") Integer id){
        try {
            var jogo = jogoService.buscarJogo(id);
            if(jogo.isPresent()){
                var responseDto = MapearJogo.toDto(jogo.get());
                return ResponseEntity.ok(responseDto);
            }

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar jogo: " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<?> ListarJogo(){
        try {
            var jogos = jogoService.listarJogo();
            List<JogoResponseDto> dtos = jogos.stream()
                    .map(MapearJogo::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar jogos: " + e.getMessage());
        }
    }

    @DeleteMapping("/{jogoId}")
    public ResponseEntity<?> deletarJogo(@PathVariable("jogoId") Integer jogoId){
        try {
            jogoService.deletarJogo(jogoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar jogo: " + e.getMessage());
        }
    }

    @PutMapping("/jogos/{jogoId}/resultado/{arbitroId}")
    public ResponseEntity<?> registrarResultado(@PathVariable Integer jogoId, @PathVariable Integer arbitroId,
                                                @Valid @RequestBody UpdateJogoDto dto) {
        try {
            jogoService.registrarResultado(jogoId, arbitroId, dto);
            Jogo jogo = jogoService.buscarJogo(jogoId)
                    .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado"));
            jogoService.verificarEAtualizarClassificacao(jogo.getGrupo());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException enfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(enfe.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar resultado: " + e.getMessage());
        }
    }
}

