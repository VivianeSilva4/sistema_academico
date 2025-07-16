package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.model.role.Role;
import jakarta.validation.constraints.*;


public record UsuarioRequestDto(@NotBlank @Size(max = 100) String nomeCompleto,
                                @NotBlank @Size(max = 50) String apelido,
                                @NotBlank @Size(max = 15) String telefone,
                                @NotBlank @Size(max = 20) String matricula,
                                @NotNull Role tipoUsuario,
                                @NotBlank @Email String email,
                                @NotBlank @Size(max = 255) String password,
                                @NotNull Integer curso) {

}
