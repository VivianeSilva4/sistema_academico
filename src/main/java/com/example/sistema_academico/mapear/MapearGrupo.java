package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.GrupoResponseDto;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.Grupo;
import com.example.sistema_academico.repository.IEventosRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class MapearGrupo {
    @Autowired
    private IEventosRepository eventosRepository;



    public GrupoResponseDto toDto(Grupo grupo){
        return new GrupoResponseDto(grupo.getId(),
                                    grupo.getNome(),
                                    grupo.getEvento().getNome());
    }
}
