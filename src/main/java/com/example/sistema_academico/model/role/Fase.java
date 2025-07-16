package com.example.sistema_academico.model.role;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Fase {
    GRUPOS("grupos"),
    QUARTAS("quartas"),
    SEMIS("semis"),
    FINAL("final");

    private String descricao;

    private Fase(String descricao){
        this.descricao = descricao;
    }
    @JsonValue
    public String getDescricao() {
        return descricao;
    }
    @JsonCreator
    public static Fase fromDescricao (String valor){
        for(Fase fase : Fase.values()){
            if(fase.descricao.equalsIgnoreCase(valor)){
                return fase;
            }
        }
        throw new IllegalArgumentException("Role inválido: " + valor);
    }
}
