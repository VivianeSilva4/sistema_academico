package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.JogoResponseDto;
import com.example.sistema_academico.dto.form.GerarJogosDto;
import com.example.sistema_academico.dto.form.JogoRequestDto;
import com.example.sistema_academico.dto.update.UpdateJogoDto;
import com.example.sistema_academico.mapear.MapearJogo;
import com.example.sistema_academico.model.*;
import com.example.sistema_academico.domain.Fase;
import com.example.sistema_academico.domain.Role;
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
    private final IEventosRepository eventosRepository;


    @Transactional
    public JogoResponseDto criarJogos(JogoRequestDto jogoDto){

        Usuario arbitro = usuarioRepository.findById(jogoDto.arbitro())
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado"));

        Grupo grupo = grupoRepository.findById(jogoDto.grupo())
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        Equipes equipeA = equipeRepository.findById(jogoDto.equipeA())
                .orElseThrow(() -> new EntityNotFoundException("Equipe A não encontrada"));

        Equipes equipeB = equipeRepository.findById(jogoDto.equipeB())
                .orElseThrow(() -> new EntityNotFoundException("Equipe B não encontrada"));

        Eventos evento = eventosRepository.findById(jogoDto.evento())
                .orElseThrow(() -> new EntityNotFoundException("evento não existe"));

        var jogoEntity = MapearJogo.toEntity(jogoDto, arbitro, grupo, equipeA, equipeB,evento);
        var salvarjogo = jogoRepository.save(jogoEntity);

        return MapearJogo.toDto(salvarjogo);
    }

    @Transactional
    public void gerarJogos(GerarJogosDto gerarJogosDto) {
        Eventos evento = eventosRepository.findById(gerarJogosDto.evento())
                .orElseThrow(() -> new EntityNotFoundException("evento não existe"));

        List<Grupo> grupos = grupoRepository.findByEvento(evento);

        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);


        if(arbitros.isEmpty()){
            throw new IllegalStateException("nenhum arbitro cadastrado");
        }
        Random random = new Random();

        for (Grupo grupo : grupos) {
            List<Equipes> equipes = new ArrayList<>(grupo.getEquipe());
            List<Jogo> jogos = gerarCombinacoesJogos(equipes, grupo, arbitros,evento, random);
            jogoRepository.saveAll(jogos);
        }
    }


    private List<Jogo> gerarCombinacoesJogos(List<Equipes> equipes, Grupo grupo,
                                             List<Usuario> arbitros,Eventos evento, Random random) {
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
                jogo.setFinalizado(false);
                jogo.setEvento(evento);
                jogos.add(jogo);
                jogo.setArbitro(arbitroSorteado);
            }
        }

        return jogos;
    }

    @Transactional
    public void registrarResultado(Integer idJogo, Integer idArbitro, UpdateJogoDto dto) {

        Jogo jogo = jogoRepository.findById(idJogo)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado"));

        Usuario arbitro = usuarioRepository.findById(idArbitro)
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado"));

        // Verifica se o usuário tem o papel de árbitro
        if (!arbitro.getTipoUsuario().equals(Role.ARBITRO)) {
            throw new IllegalStateException("Usuário não é um árbitro autorizado.");
        }

        // Verifica se é o árbitro responsável por esse jogo
        if (!jogo.getArbitro().getIdUsuario().equals(arbitro.getIdUsuario())) {
            throw new IllegalStateException("Este árbitro não está autorizado a registrar o resultado deste jogo.");
        }

        // Impede alteração de resultados já finalizados
        if (jogo.isFinalizado()) {
            throw new IllegalStateException("Resultado já registrado para este jogo.");
        }

        // Impede empate em fases eliminatórias
        if (dto.placaA().equals(dto.placaB()) && (jogo.getFase().equals(Fase.QUARTAS)
                ||dto.placaA().equals(dto.placaB()) && jogo.getFase().equals(Fase.SEMISFINAL)
                ||dto.placaA().equals(dto.placaB()) &&  jogo.getFase().equals(Fase.FINAL))) {

            throw new IllegalStateException("Não é permitido empate nessa fase.");
        }

        // Atualiza os dados do jogo
        jogo.setPlacaA(dto.placaA());
        jogo.setPlacaB(dto.placaB());
        jogo.setDataHora(LocalDateTime.now());
        jogo.setWoA(dto.woA());
        jogo.setWoB(dto.woB());

        // Define se está finalizado automaticamente
        boolean finalizado = (dto.placaA() != null && dto.placaB() != null)
                || Boolean.TRUE.equals(dto.woA())
                || Boolean.TRUE.equals(dto.woB());

        jogo.setFinalizado(finalizado);

        jogoRepository.save(jogo);

        // Atualiza classificação apenas se for fase de grupos
        if (finalizado && jogo.getFase().equals(Fase.GRUPOS)) {
            classificacaoGrupoService.atualizarClassificacaoPorGrupo(jogo.getGrupo());
        }
    }

    @Transactional
    public void verificarEAtualizarClassificacao(Grupo grupo) {
        List<Jogo> jogosDoGrupo = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);
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
