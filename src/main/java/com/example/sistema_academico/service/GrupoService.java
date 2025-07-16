package com.example.sistema_academico.service;



import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Grupo;
import com.example.sistema_academico.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final IGrupoRepository grupoRepository;
    private final IEventosRepository eventoRepository;
    private final IEquipeRepository equipeRepository;
    private final JogoService jogoService;

    public void gerarGrupo(Integer id){
        var evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        List<Grupo> gruposAntigos = grupoRepository.findByEvento(evento);
        grupoRepository.deleteAll(gruposAntigos);

        List<Equipes> listaEquipe = equipeRepository.findAll();
        Collections.shuffle(listaEquipe);
        List<List<Equipes>> gruposDividido = divideEmGrupos(listaEquipe);

        int contador = 1;
        for(List<Equipes> subGrupo : gruposDividido){
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + contador);
            grupo.setEvento(evento);
            grupo.setEquipe(subGrupo);
            grupoRepository.save(grupo);
            contador++;
        }

    }
    private  List<List<Equipes>> divideEmGrupos(List<Equipes> equipe){
        List<List<Equipes>> grupos = new ArrayList<>();
        int inicio = 0;

        while(inicio < equipe.size()){
            int resto = equipe.size() - inicio;

            if(resto > 5){
                grupos.add(equipe.subList(inicio,inicio+3));
                inicio+=3;
            }else if(resto == 4){
                grupos.add(equipe.subList(inicio,inicio+4));
                inicio+=4;
            }else if(resto == 5){
                grupos.add(equipe.subList(inicio,inicio+5));
                inicio+=5;
            }else if(resto == 3){
                grupos.add(equipe.subList(inicio,inicio+3));
                inicio+=3;
            }else{
                throw new IllegalStateException("Não é possível grupo com menos de 3 equipes");
            }
        }
        return grupos;
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
