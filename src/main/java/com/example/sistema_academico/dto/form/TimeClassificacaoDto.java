package com.example.sistema_academico.dto.form;

import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Grupo;

public record TimeClassificacaoDto(Integer equipe, Integer grupo, boolean primeiroDoGrupo) {
}
