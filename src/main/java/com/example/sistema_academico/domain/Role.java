package com.example.sistema_academico.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    TECNICO("tecnico"),
    COORDENADOR("coordenador"),
    ARBITRO("arbitro"),
    ATLETA("atleta");

    private String descricao;

    private Role(String descricao){
        this.descricao = descricao;
    }
    @JsonValue
    public String getDescricao() {
        return descricao;
    }
    @JsonCreator
     public static Role fromDescricao (String valor){
        for(Role role : Role.values()){
            if(role.descricao.equalsIgnoreCase(valor)){
                return role;
            }
        }
        throw new IllegalArgumentException("Role inválido: " + valor);
     }
}
