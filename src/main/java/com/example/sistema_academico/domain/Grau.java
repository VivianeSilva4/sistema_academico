package com.example.sistema_academico.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Grau {
    INTEGRADO("integrado"),
    SUPERIOR("superior"),
    TECNICO("tecnico");

    private String descricao;

    private Grau(String descricao){
        this.descricao = descricao;
    }
    @JsonValue
    public String getDescricao() {
        return descricao;
    }
    @JsonCreator
    public static Grau fromDescricao (String valor){
        for(Grau grau : Grau.values()){
            if(grau.descricao.equalsIgnoreCase(valor)){
                return grau;
            }
        }
        throw new IllegalArgumentException("Grau inválido: " + valor);
    }

}
