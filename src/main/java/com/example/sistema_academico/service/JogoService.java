package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
import com.example.sistema_academico.dto.form.JogoRequestDto;
import com.example.sistema_academico.dto.update.UpdateJogoDto;
import com.example.sistema_academico.mapear.MapearJogo;
import com.example.sistema_academico.model.*;
import com.example.sistema_academico.model.role.Fase;
import com.example.sistema_academico.model.role.Role;
import com.example.sistema_academico.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final IJogoRepository jogoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IGrupoRepository grupoRepository;
    private final IEquipeRepository equipeRepository;
    private final ClassificacaoPorGrupoService classificacaoGrupoService;


    @Transactional
    public JogoResponseDto criarJogos(JogoRequestDto jogoDto){

        Usuario arbitro = usuarioRepository.findById(jogoDto.arbitro())
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado"));

        Grupo grupo = grupoRepository.findById(jogoDto.grupo())
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        Equipes equipeA = equipeRepository.findById(jogoDto.equipeA())
                .orElseThrow(() -> new EntityNotFoundException());

        Equipes equipeB = equipeRepository.findById(jogoDto.equipeB())
                .orElseThrow(() -> new EntityNotFoundException());

        var jogoEntity = MapearJogo.toEntity(jogoDto, arbitro, grupo, equipeA, equipeB);
        var salvarjogo = jogoRepository.save(jogoEntity);

        return MapearJogo.toDto(salvarjogo);
    }

    @Transactional
    public void gerarJogos() {
        List<Grupo> grupos = grupoRepository.findAll();
        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);

        if(arbitros.isEmpty()){
            throw new IllegalStateException("nenhum arbitro cadastrado");
        }
        Random random = new Random();


        for (Grupo grupo : grupos) {
            List<Equipes> equipes = new ArrayList<>(grupo.getEquipe());
            List<Jogo> jogos = gerarCombinacoesJogos(equipes, grupo, arbitros,  random);
            jogoRepository.saveAll(jogos);
        }
    }


    private List<Jogo> gerarCombinacoesJogos(List<Equipes> equipes, Grupo grupo,
                                             List<Usuario> arbitros, Random random) {
        List<Jogo> jogos = new ArrayList<>();


        for (int i = 0; i < equipes.size(); i++) {
            for (int j = i + 1; j < equipes.size(); j++) {
                Equipes equipeA = equipes.get(i);
                Equipes equipeB = equipes.get(j);
                Usuario arbitroSorteado = arbitros.get(random.nextInt(arbitros.size()));

                Jogo jogo = new Jogo();
                jogo.setGrupo(grupo);
                jogo.setEquipeA(equipeA);
                jogo.setEquipeB(equipeB);
                jogo.setFase(Fase.GRUPOS);
                jogo.setDataHora(LocalDateTime.now());
                jogo.setPlacaA(null);
                jogo.setPlacaB(null);
                jogo.setWoA(false);
                jogo.setWoB(false);
                jogos.add(jogo);
                jogo.setArbitro(arbitroSorteado);
            }
        }

        return jogos;
    }

    @Transactional
    public void registrarResultado(Integer idJogo, UpdateJogoDto dto) {
        Jogo jogo = jogoRepository.findById(idJogo)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado"));


        if (jogo.getPlacaA() != null || jogo.getPlacaB() != null || Boolean.TRUE.equals(jogo.getWoA()) || Boolean.TRUE.equals(jogo.getWoB())) {
            throw new IllegalStateException("Resultado já registrado para este jogo.");
        }

        jogo.setPlacaA(dto.placaA());
        jogo.setPlacaB(dto.placaB());
        jogo.setDataHora(LocalDateTime.now());
        jogo.setWoA(dto.woA());
        jogo.setWoB(dto.woB());

        jogoRepository.save(jogo);

    }
    @Transactional
    public void verificarEAtualizarClassificacao(Grupo grupo) {
        List<Jogo> jogosDoGrupo = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);

        boolean todosFinalizados = jogosDoGrupo.stream().allMatch(j ->
                (j.getPlacaA() != null && j.getPlacaB() != null)
                        || Boolean.TRUE.equals(j.getWoA())
                        || Boolean.TRUE.equals(j.getWoB())
        );

        if (todosFinalizados) {
            classificacaoGrupoService.atualizarClassificacaoPorGrupo(grupo);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Jogo> buscarJogo(Integer id){
        return jogoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Jogo> listarJogo(){
        return jogoRepository.findAll();
    }

    @Transactional
    public void deletarJogo(Integer id){
        if(jogoRepository.existsById(id)){
            jogoRepository.deleteById(id);
        }
    }

}
