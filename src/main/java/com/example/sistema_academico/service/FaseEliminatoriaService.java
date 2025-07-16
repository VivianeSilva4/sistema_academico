package com.example.sistema_academico.service;

import com.example.sistema_academico.model.*;
import com.example.sistema_academico.model.role.Fase;
import com.example.sistema_academico.repository.IClassificacaoGrupoRepository;
import com.example.sistema_academico.repository.IEventosRepository;
import com.example.sistema_academico.repository.IJogoRepository;
import com.example.sistema_academico.repository.IGrupoRepository;
import com.example.sistema_academico.repository.IFaseEliminatoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaseEliminatoriaService {

    private final IEventosRepository eventoRepository;
    private final IGrupoRepository grupoRepository;
    private final IJogoRepository jogoRepository;
    private final IClassificacaoGrupoRepository classificacaoGrupoRepository;
    private final ClassificacaoPorGrupoService classificacaoPorGrupoService;
    private final IFaseEliminatoriaRepository faseEliminatoriaRepository;

    @Transactional
    public void gerarFaseEliminatoria(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado!"));

        if (!todosJogosDeGrupoConcluidos(evento)) {
            throw new IllegalStateException("Nem todos os jogos da fase " +
                    "de grupos foram concluídos ou WO informados.");
        }

        List<Equipes> classificados = getClassificadosParaEliminatoria(evento);

        faseEliminatoriaRepository.deleteByEventoIdAndJogoFaseIn(eventoId, List.of(Fase.SEMIS, Fase.FINAL, Fase.QUARTAS));
        jogoRepository.deleteJogosByEventoAndFases(eventoId, List.of(Fase.SEMIS, Fase.FINAL, Fase.QUARTAS));

        List<Jogo> jogosEliminatoriosGerados = new ArrayList<>();

        if (classificados.size() == 4) {
            jogosEliminatoriosGerados.addAll(gerarSemifinais4Equipes(classificados, evento));
        } else if (classificados.size() == 6) {
            jogosEliminatoriosGerados.addAll(gerarQuartas6Equipes(classificados, evento));
        } else if (classificados.size() == 8) {
            jogosEliminatoriosGerados.addAll(gerarQuartas8Equipes(classificados, evento));
        } else {
            throw new UnsupportedOperationException("A geração da fase eliminatória para "
                    + classificados.size() + " equipes não é suportada atualmente.");
        }

        jogoRepository.saveAll(jogosEliminatoriosGerados);

        List<FaseEliminatoria> entradasFaseEliminatoria = new ArrayList<>();
        for (Jogo jogo : jogosEliminatoriosGerados) {
            FaseEliminatoria fe = new FaseEliminatoria();
            fe.setEvento(evento);
            fe.setJogo(jogo);
            fe.setTipo(jogo.getFase());
            entradasFaseEliminatoria.add(fe);
        }
        faseEliminatoriaRepository.saveAll(entradasFaseEliminatoria);
    }

    private boolean todosJogosDeGrupoConcluidos(Eventos evento) {
        List<Grupo> grupos = grupoRepository.findByEvento(evento);
        for (Grupo grupo : grupos) {
            List<Jogo> jogosDoGrupo = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);
            for (Jogo jogo : jogosDoGrupo) {
                if (jogo.getPlacaA() == null && jogo.getPlacaB() == null && !Boolean.TRUE.equals(jogo.getWoA()) && !Boolean.TRUE.equals(jogo.getWoB())) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<Equipes> getClassificadosParaEliminatoria(Eventos evento) {
        List<Equipes> classificados = new ArrayList<>();
        List<Grupo> grupos = grupoRepository.findByEvento(evento);

        for (Grupo grupo : grupos) {
            classificacaoPorGrupoService.atualizarClassificacaoPorGrupo(grupo);

            List<ClassificacaoGrupo> classificacoes = classificacaoGrupoRepository
                    .findByGrupo(grupo);
            classificacoes.sort(Comparator
                    .comparingInt(ClassificacaoGrupo::getPontos).reversed()
                    .thenComparingInt(ClassificacaoGrupo::getSaldo_gols).reversed());

            if (classificacoes.size() >= 2) {
                classificados.add(classificacoes.get(0).getEquipe());
                classificados.add(classificacoes.get(1).getEquipe());
            } else if (classificacoes.size() == 1) {
                classificados.add(classificacoes.get(0).getEquipe());
            }
        }
        return classificados;
    }

    private List<Jogo> gerarQuartas8Equipes(List<Equipes> classificados, Eventos evento) {
        List<Jogo> jogos = new ArrayList<>();

        List<Equipes> primeirosColocados = new ArrayList<>();
        List<Equipes> segundosColocados = new ArrayList<>();

        List<Grupo> grupos = grupoRepository.findByEvento(evento);

        for(Grupo grupo : grupos) {
            List<ClassificacaoGrupo> classificacoes = classificacaoGrupoRepository
                    .findByGrupo(grupo);
            classificacoes.sort(Comparator
                    .comparingInt(ClassificacaoGrupo::getPontos).reversed()
                    .thenComparingInt(ClassificacaoGrupo::getSaldo_gols).reversed());
            if (!classificacoes.isEmpty()) {
                primeirosColocados.add(classificacoes.get(0).getEquipe());
                if (classificacoes.size() > 1) {
                    segundosColocados.add(classificacoes.get(1).getEquipe());
                }
            }
        }

        if (primeirosColocados.size() >= 4 && segundosColocados.size() >= 4) {
            jogos.add(criarJogo(Fase.QUARTAS, primeirosColocados.get(0), segundosColocados.get(3), evento));
            jogos.add(criarJogo(Fase.QUARTAS, primeirosColocados.get(1), segundosColocados.get(2), evento));
            jogos.add(criarJogo(Fase.QUARTAS, primeirosColocados.get(2), segundosColocados.get(1), evento));
            jogos.add(criarJogo(Fase.QUARTAS, primeirosColocados.get(3), segundosColocados.get(0), evento));
        }

        return jogos;
    }

    private List<Jogo> gerarQuartas6Equipes(List<Equipes> classificados, Eventos evento) {
        List<Jogo> jogos = new ArrayList<>();

        classificados.sort(Comparator
                .comparing((Equipes e) -> classificacaoGrupoRepository
                        .findByEquipeAndGrupoEvento(e, evento)
                        .map(ClassificacaoGrupo::getPontos).orElse(0)).reversed()
                .thenComparing((Equipes e) -> classificacaoGrupoRepository
                        .findByEquipeAndGrupoEvento(e, evento)
                        .map(ClassificacaoGrupo::getSaldo_gols).orElse(0)).reversed());

        if (classificados.size() >= 4) {
            jogos.add(criarJogo(Fase.QUARTAS, classificados.get(2), classificados.get(5), evento));
            jogos.add(criarJogo(Fase.QUARTAS, classificados.get(3), classificados.get(4), evento));
        }

        return jogos;
    }

    private List<Jogo> gerarSemifinais4Equipes(List<Equipes> classificados, Eventos evento) {
        List<Jogo> jogos = new ArrayList<>();

        if (classificados.size() == 4) {
            jogos.add(criarJogo(Fase.SEMIS, classificados.get(0), classificados.get(3), evento));
            jogos.add(criarJogo(Fase.SEMIS, classificados.get(2), classificados.get(1), evento));
        } else {
            throw new IllegalStateException("Número de equipes para semifinal não é 4.");
        }

        return jogos;
    }

    private Jogo criarJogo(Fase fase, Equipes equipeA, Equipes equipeB, Eventos evento) {
        Jogo jogo = new Jogo();
        jogo.setFase(fase);
        jogo.setEquipeA(equipeA);
        jogo.setEquipeB(equipeB);
        jogo.setDataHora(LocalDateTime.now());
        jogo.setWoA(false);
        jogo.setWoB(false);

        return jogo;
    }
}
