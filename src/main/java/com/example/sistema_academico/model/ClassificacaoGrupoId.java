package com.example.sistema_academico.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor @NoArgsConstructor

public class ClassificacaoGrupoId implements Serializable {

    @Column(name ="fk_equipe")
    private Integer equipeId;

    @Column(name ="fk_grupo")
    private Integer grupoId;
}
