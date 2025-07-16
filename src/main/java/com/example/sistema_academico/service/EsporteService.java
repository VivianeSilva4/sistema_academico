package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.EsporteResponseDto;
import com.example.sistema_academico.dto.form.EsporteRequestDto;
import com.example.sistema_academico.mapear.MapearEsporte;
import com.example.sistema_academico.model.Esportes;
import com.example.sistema_academico.repository.IEsporteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EsporteService {

    private IEsporteRepository esporteRepository;

    @Transactional
    public EsporteResponseDto salvarEsporte(EsporteRequestDto esporteDto){
        if(esporteRepository.findByNomeIgnoreCase(esporteDto.nome()).isPresent()){
            throw new IllegalArgumentException("Já existe um esporte com esse nome");
        }
        var esporte = esporteRepository.save(MapearEsporte.toEntity(esporteDto));
        return MapearEsporte.toDto(esporte);
    }
    @Transactional(readOnly = true)
    public Optional<Esportes> buscarEsporte(Integer id){
        return esporteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Esportes> listarEsporte(){
        return esporteRepository.findAll();
    }

    @Transactional
    public void deletarEsporte(Integer id){
        var esporteExist =  esporteRepository.existsById(id);

        if(esporteExist){
            esporteRepository.deleteById(id);
        }

    }


}
