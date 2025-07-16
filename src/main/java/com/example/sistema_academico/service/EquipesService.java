package com.example.sistema_academico.service;


import com.example.sistema_academico.dto.Response.EquipeResponseDto;
import com.example.sistema_academico.dto.form.EquipeRequestDto;
import com.example.sistema_academico.mapear.MapearEquipe;
import com.example.sistema_academico.model.*;
import com.example.sistema_academico.model.role.Role;
import com.example.sistema_academico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipesService {

    private final IEquipeRepository equipesRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ICursoRepository cursoRepository;
    private final IEsporteRepository esporteRepository;

    @Transactional
    public EquipeResponseDto criarEquipe(EquipeRequestDto equipeDto){

        Usuario tecnicoDaEquipe = usuarioRepository.findById(equipeDto.tecnicoDaEquipe())
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));

        if (!tecnicoDaEquipe.getTipoUsuario().equals(Role.TECNICO)) {
            throw new SecurityException("Apenas técnicos podem cadastrar equipes.");
        }

        Esportes esporte = esporteRepository.findById(equipeDto.esporte())
                .orElseThrow(() -> new RuntimeException("Esporte não encontrado"));

        Cursos curso = cursoRepository.findById(equipeDto.curso())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        Optional<Equipes> equipeDoTecnico = equipesRepository.findFirstByTecnicoDaEquipe(tecnicoDaEquipe);
        if (equipeDoTecnico.isPresent()) {
            throw new IllegalArgumentException("Técnico já está vinculado a uma equipe.");
        }

        if (equipesRepository.existsByEsporteAndCurso(esporte, curso)) {
            throw new IllegalArgumentException("Já existe uma equipe para este esporte neste curso.");
        }

        List<Usuario> atletas = usuarioRepository.findAllById(equipeDto.atletas());

        for (Usuario atleta : atletas) {
            if (!atleta.getTipoUsuario().equals(Role.ATLETA)) {
                throw new IllegalArgumentException("Usuário " + atleta.getNomeCompleto()
                        + " não é um atleta.");
            }
            if (!curso.equals(atleta.getCurso())) {
                throw new IllegalArgumentException("Usuário " + atleta.getNomeCompleto()
                        + " não pertence ao curso selecionado.");
            }
        }

        var equipeEntity = MapearEquipe.toEntity(equipeDto, tecnicoDaEquipe, esporte, curso);
        equipeEntity.setAtletasPorEquipe(atletas);

        var salvarEquipe = equipesRepository.save(equipeEntity);

        return MapearEquipe.toDto(salvarEquipe);
    }


    @Transactional(readOnly = true)
    public Optional<Equipes> buscarEquipes(Integer id){
           return equipesRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Equipes> listarEquipes(){
        return equipesRepository.findAll();
    }

    @Transactional
    public void deletarEquipe(Integer id){
        if(equipesRepository.existsById(id)){
            equipesRepository.deleteById(id);
        }
    }

}
