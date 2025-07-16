package com.example.sistema_academico.service;


import com.example.sistema_academico.dto.Response.CursoResponseDto;
import com.example.sistema_academico.dto.form.CursoRequestDto;
import com.example.sistema_academico.mapear.MapearCurso;
import com.example.sistema_academico.dto.update.UpdateCursoDto;
import com.example.sistema_academico.model.Cursos;
import com.example.sistema_academico.repository.ICampusRepository;
import com.example.sistema_academico.repository.ICursoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final ICursoRepository cursoRepository;
    private final ICampusRepository campusRepository;

    @Transactional
    public CursoResponseDto salvarCurso(CursoRequestDto cursoDto){
        var campus = campusRepository.findById(cursoDto.campus())
                .orElseThrow(() ->new EntityNotFoundException("Campus não encontrado"));
        var existe = cursoRepository.existsByNomeAndCampus(cursoDto.nome(), campus);
        if(existe){
            throw new IllegalArgumentException("já existe esse curso neste campus");
        }
        MapearCurso mapearCurso = new MapearCurso();
        var curso = cursoRepository.save(mapearCurso.toEntity(cursoDto));
         return MapearCurso.toDto(curso);
    }
    @Transactional(readOnly = true)
    public Optional<Cursos> buscarCurso(Integer id){
        return cursoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Cursos> listarCurso(){
        return cursoRepository.findAll();
    }

    @Transactional
    public void deletarCurso(Integer id){
        var cursoExist =  cursoRepository.existsById(id);

        if(cursoExist){
            cursoRepository.deleteById(id);
        }
    }
    @Transactional
    public void atualizarDados(Integer id, UpdateCursoDto cursoDto){
        var cursoEntity = cursoRepository.findById(id);

        if(cursoEntity.isPresent()){
            var curso = cursoEntity.get();

            if(cursoDto.nome() != null){
               curso.setNome(cursoDto.nome());
            }


            cursoRepository.save(curso);
        }
    }
}
