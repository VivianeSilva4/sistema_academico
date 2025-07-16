package com.example.sistema_academico.dto.Response;

import com.example.sistema_academico.model.role.Role;


import java.util.Date;

public record UsuarioResponseDto(Integer idUsuario,
                                 String nomeCompleto,
                                 String apelido,
                                 String telefone,
                                 String matricula,
                                 String email,
                                 Role tipoUsuario,
                                 Date dataCriacao,
                                 boolean ativo,
                                 String curso) {
}
