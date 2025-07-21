package com.example.sistema_academico.service;



import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.Grupo;
import com.example.sistema_academico.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final IGrupoRepository grupoRepository;
    private final IEventosRepository eventoRepository;
    private final IEquipeRepository equipeRepository;


    @Transactional
    public void gerarGrupos(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado com ID: "
                        + eventoId));

        limparGruposExistentes(evento);

        List<Equipes> todasAsEquipes = equipeRepository.findAll();
        validarNumeroMinimoDeEquipes(todasAsEquipes);

        Collections.shuffle(todasAsEquipes);

        List<List<Equipes>> gruposDivididos = dividirEquipesEmSubgrupos(todasAsEquipes);

        salvarNovosGrupos(gruposDivididos, evento);
    }

    private void limparGruposExistentes(Eventos evento) {
        List<Grupo> gruposAntigos = grupoRepository.findByEvento(evento);
        if (!gruposAntigos.isEmpty()) {
            grupoRepository.deleteAll(gruposAntigos);
        }
    }

    private void validarNumeroMinimoDeEquipes(List<Equipes> equipes) {
        if (equipes.size() < 3) {
            throw new IllegalStateException("É necessário ter no mínimo 3 equipes " +
                    "para gerar grupos.");
        }
    }

    private List<List<Equipes>> dividirEquipesEmSubgrupos(List<Equipes> equipes) {
        int totalEquipes = equipes.size();
        List<List<Equipes>> gruposFormados = new ArrayList<>();

        List<Integer> tamanhosDosGrupos = calcularTamanhosDosGrupos(totalEquipes);

        int indiceInicial = 0;

        for (int tamanhoDoGrupo : tamanhosDosGrupos) {

            if (indiceInicial + tamanhoDoGrupo <= equipes.size()) {
                gruposFormados.add(equipes.subList(indiceInicial, indiceInicial + tamanhoDoGrupo));
                indiceInicial += tamanhoDoGrupo;
            } else {
                throw new IllegalStateException("Erro na divisão de equipes: o número de " +
                        "equipes restantes não corresponde ao tamanho esperado do grupo. " +
                        "Total de equipes: " + totalEquipes + ", Tamanho do grupo esperado: "
                        + tamanhoDoGrupo + ", Equipes restantes a alocar: " + (equipes.size()
                        - indiceInicial));
            }

        }
        return gruposFormados;
    }

    private void salvarNovosGrupos(List<List<Equipes>> subgrupos, Eventos evento) {
        char nomeGrupoChar = 'A';
        for (List<Equipes> subGrupoEquipes : subgrupos) {
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + nomeGrupoChar);
            grupo.setEvento(evento);
            grupo.setEquipe(subGrupoEquipes);
            grupoRepository.save(grupo);
            nomeGrupoChar++;
        }
    }

    private List<Integer> calcularTamanhosDosGrupos(int totalEquipes) {
        switch (totalEquipes) {
            case 3: return Arrays.asList(3);
            case 4: return Arrays.asList(4);
            case 5: return Arrays.asList(5);
            case 6: return Arrays.asList(3, 3);
            case 7: return Arrays.asList(3, 4);
            case 8: return Arrays.asList(4, 4);
            case 9: return Arrays.asList(3, 3, 3);
            case 10: return Arrays.asList(4, 3, 3);
            case 11: return Arrays.asList(3, 4, 4);
            case 12: return Arrays.asList(3, 3, 3, 3);
        }

        if (totalEquipes > 12) {
            List<Integer> tamanhos = new ArrayList<>();
            int equipesRestantes = totalEquipes;

            while (equipesRestantes >= 8) {
                tamanhos.add(4);
                equipesRestantes -= 4;
            }

            switch(equipesRestantes) {
                case 3: tamanhos.add(3); break;
                case 4: tamanhos.add(4); break;
                case 5: tamanhos.add(5); break;
                case 6: tamanhos.addAll(Arrays.asList(3, 3)); break;
                case 7: tamanhos.addAll(Arrays.asList(3, 4)); break;
                default:
                    throw new IllegalStateException("Erro inesperado na lógica de distribuição" +
                            " para " + equipesRestantes + " equipes restantes.");
            }
            return tamanhos;
        }

        throw new IllegalStateException("Não foi possível determinar a divisão de grupos para "
                + totalEquipes + " equipes. Verifique as regras de negócio.");
    }

    @Transactional(readOnly = true)
    public Optional<Grupo> buscarGrupo(Integer id){
        return grupoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Grupo> listarGrupo(){
        return grupoRepository.findAll();
    }

    @Transactional
    public void deletarGrupo(Integer id){
        var grupoExist =  grupoRepository.existsById(id);

        if(grupoExist){
            grupoRepository.deleteById(id);
        }
    }
}
