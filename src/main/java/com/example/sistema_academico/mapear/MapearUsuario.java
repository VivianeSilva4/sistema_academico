package com.example.sistema_academico.mapear;

import com.example.sistema_academico.dto.Response.UsuarioResponseDto;
import com.example.sistema_academico.dto.form.UsuarioRequestDto;
import com.example.sistema_academico.model.Cursos;
import com.example.sistema_academico.model.Usuario;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MapearUsuario {

    public static Usuario toEntity(UsuarioRequestDto usuario, Cursos curso) {

        Date dataCriacao = Date.from(ZonedDateTime.now(ZoneId
                .of("America/Sao_Paulo")).toInstant());

        return new Usuario(
                null,
                usuario.nomeCompleto(),
                usuario.apelido(),
                usuario.telefone(),
                usuario.matricula(),
                usuario.tipoUsuario(),
                usuario.email(),
                usuario.password(),
                dataCriacao,curso,
                null,
                null,
                null);

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
                usuario.getCurso() != null ? usuario.getCurso().getNome() : null
        );
    }

}
