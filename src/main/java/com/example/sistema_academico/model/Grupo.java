package com.example.sistema_academico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "grupos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer id;

    @NotBlank
    private String nome;

    @OneToMany(mappedBy = "grupo",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ClassificacaoGrupo> classificacaoGrupos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fk_eventos")
    private Eventos evento;

    @ManyToMany
    @JoinTable(
            name = "grupos_equipes",
            joinColumns = {@JoinColumn (name = "fk_grupo")},
            inverseJoinColumns = {@JoinColumn (name = "fk_equipe")}
    )
    private Collection<Equipes> equipe;

    @OneToMany(mappedBy = "grupo",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Jogo> jogo = new ArrayList<>();
}
