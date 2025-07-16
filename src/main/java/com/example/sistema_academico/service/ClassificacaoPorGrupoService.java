package com.example.sistema_academico.service;

import com.example.sistema_academico.model.*;
import com.example.sistema_academico.model.role.Fase;
import com.example.sistema_academico.repository.IClassificacaoGrupoRepository;
import com.example.sistema_academico.repository.IJogoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClassificacaoPorGrupoService {
    private final IJogoRepository jogoRepository;
    private final IClassificacaoGrupoRepository classificacaoRepository;

    @Transactional
    public void atualizarClassificacaoPorGrupo(Grupo grupo) {

        List<Jogo> jogos = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);

        Map<Equipes, ClassificacaoGrupo> mapa = new HashMap<>();

        for (Jogo jogo : jogos) {


            Equipes equipeA = jogo.getEquipeA();
            Equipes equipeB = jogo.getEquipeB();


            ClassificacaoGrupo classificA = mapa.computeIfAbsent(equipeA, eq -> criarBase(grupo, eq));
            ClassificacaoGrupo classificB = mapa.computeIfAbsent(equipeB, eq -> criarBase(grupo, eq));

            int golsA = jogo.getPlacaA();
            int golsB = jogo.getPlacaB();

            classificA.setSaldo_gols(classificA.getSaldo_gols() + (golsA - golsB));
            classificB.setSaldo_gols(classificB.getSaldo_gols() + (golsB - golsA));

            if (Boolean.TRUE.equals(jogo.getWoA())) {

                contabilizarVitoria(classificB);
                contabilizarDerrota(classificA);

            } else if (Boolean.TRUE.equals(jogo.getWoB())) {

                contabilizarVitoria(classificA);
                contabilizarDerrota(classificB);

            } else if (golsA > golsB) {

                contabilizarVitoria(classificA);
                contabilizarDerrota(classificB);

            } else if (golsB > golsA) {
                contabilizarVitoria(classificB);
                contabilizarDerrota(classificA);

            } else {

                classificA.setEmpates(classificA.getEmpates() + 1);
                classificB.setEmpates(classificB.getEmpates() + 1);
                classificA.setPontos(classificA.getPontos() + 1);
                classificB.setPontos(classificB.getPontos() + 1);
            }
        }

        classificacaoRepository.deleteByGrupo(grupo);
        classificacaoRepository.saveAll(mapa.values());
    }

    private ClassificacaoGrupo criarBase(Grupo grupo, Equipes equipe) {
        ClassificacaoGrupo classificacao = new ClassificacaoGrupo();
        classificacao.setId(new ClassificacaoGrupoId(grupo.getId(), equipe.getId()));
        classificacao.setGrupo(grupo);
        classificacao.setEquipe(equipe);
        classificacao.setPontos(0);
        classificacao.setEmpates(0);
        classificacao.setVitorias(0);
        classificacao.setDerrotas(0);
        classificacao.setSaldo_gols(0);
        return classificacao;
    }

    private void contabilizarVitoria(ClassificacaoGrupo c) {
        c.setVitorias(c.getVitorias() + 1);
        c.setPontos(c.getPontos() + 3);
    }

    private void contabilizarDerrota(ClassificacaoGrupo c) {
        c.setDerrotas(c.getDerrotas() + 1);
    }

}
