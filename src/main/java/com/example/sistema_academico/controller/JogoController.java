package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
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
    public ResponseEntity<JogoResponseDto> criarJogos(@Valid @RequestBody JogoRequestDto jogoDto){
       var jogo = jogoService.criarJogos(jogoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(jogo);
    }
    @PostMapping("/jogos/gerar")
    public ResponseEntity<Void> gerarJogos() {
        jogoService.gerarJogos();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{jogoId}")
    public ResponseEntity<JogoResponseDto> getById(@PathVariable("jogoId") Integer id){
        var jogo = jogoService.buscarJogo(id);
        if(jogo.isPresent()){
            var responseDto = MapearJogo.toDto(jogo.get());
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<JogoResponseDto>> ListarJogo(){
        var jogos = jogoService.listarJogo();

        List<JogoResponseDto> dtos = jogos.stream()
                .map(MapearJogo::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
    @DeleteMapping("/{jogoId}")
    public ResponseEntity<Void> deletarJogo(@PathVariable("jogoId") Integer jogoId){
        jogoService.deletarJogo(jogoId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/jogos/{jogoId}/resultado")
    public ResponseEntity<Void> registrarResultado(@PathVariable Integer jogoId,
                                                   @Valid @RequestBody UpdateJogoDto dto) {
        jogoService.registrarResultado(jogoId, dto);
        Jogo jogo = jogoService.buscarJogo(jogoId)
                .orElseThrow(() -> new EntityNotFoundException("jogo não encontrado"));
        jogoService.verificarEAtualizarClassificacao(jogo.getGrupo());
        return ResponseEntity.ok().build();
    }

}
