package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.CampusResponseDto;
import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.mapear.MapearCampus;
import com.example.sistema_academico.dto.update.UpdateCampusDto;
import com.example.sistema_academico.model.Campus;
import com.example.sistema_academico.repository.ICampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampusService {

    private final ICampusRepository campusRepository;

    @Transactional
    public CampusResponseDto salvar(CampusRequestDto campusDto){
        if(campusRepository.existsByNomeAndEndereco(campusDto.nome(), campusDto.endereco())){
            throw new IllegalArgumentException("Já existe um campus com esse nome e enderço");
        }
        var campusEntity = MapearCampus.toEntity(campusDto);
        var salvarCampus = campusRepository.save(campusEntity);
        return MapearCampus.toDto(salvarCampus);
    }

    @Transactional(readOnly = true)
    public Optional<Campus> buscarCampus(Integer id){
        return campusRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Campus> listarCampus(){
        return campusRepository.findAll();
    }

    @Transactional
    public void deletarCampus(Integer id){

        if(campusRepository.existsById(id)){
            campusRepository.deleteById(id);
        }
    }
    @Transactional
    public void atualizarDados(Integer id, UpdateCampusDto campusDto){
        var campusEntity = campusRepository.findById(id);

        if(campusEntity.isPresent()){
            var campus = campusEntity.get();

            if(campusDto.nome() != null){
                campus.setNome(campusDto.nome());
            }
            if(campusDto.endereco() != null){
                campus.setEndereco(campusDto.endereco());
            }

            campusRepository.save(campus);
        }
    }
}
