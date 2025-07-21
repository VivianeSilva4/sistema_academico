package com.example.sistema_academico.model;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "classificacao_grupo")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ClassificacaoGrupo {

    @EmbeddedId
    @EqualsAndHashCode.Include
    @ToString.Include
    private ClassificacaoGrupoId id = new ClassificacaoGrupoId();

    @ManyToOne
    @MapsId("grupoId")
    @JoinColumn(name = "fk_grupo")
    private Grupo grupo;

    @ManyToOne
    @MapsId("equipeId")
    @JoinColumn(name = "fk_equipe")
    private Equipes equipe;

    private int vitorias;

    private int pontos;

    private int derrotas;

    private int empates;

    @Column(name ="saldo_gols")
    private int saldoGols;


}
