package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.UsuarioResponseDto;
import com.example.sistema_academico.dto.form.UsuarioRequestDto;
import com.example.sistema_academico.model.Cursos;
import com.example.sistema_academico.model.Usuario;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MapearUsuario {

    public static Usuario toEntity(UsuarioRequestDto dto, Cursos curso) {

        Date dataCriacao = Date.from(ZonedDateTime.now(ZoneId
                .of("America/Sao_Paulo")).toInstant());

        return new Usuario(null,
                dto.nomeCompleto(),
                dto.apelido(),
                dto.telefone(),
                dto.matricula(),
                dto.tipoUsuario(),
                dto.email(),
                dto.password(),
                dataCriacao, false,
                curso, null, null, null);

    }
    public static UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getNomeCompleto(),
                usuario.getApelido(),
                usuario.getTelefone(),
                usuario.getMatricula(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getDataCriacao(),
                usuario.isAtivo(),
                usuario.getCurso() != null ? usuario.getCurso().getNome() : null
        );
    }

}
