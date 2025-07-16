package com.example.sistema_academico.model;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@EqualsAndHashCode
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ClassificacaoGrupoId {

    @Column(name ="fk_equipe")
    private Integer equipeId;

    @Column(name ="fk_grupo")
    private Integer grupoId;
}
