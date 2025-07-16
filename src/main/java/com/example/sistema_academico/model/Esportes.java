package com.example.sistema_academico.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "esportes")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Esportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_esporte")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer id;

    private String nome;

    @Column(name = "min_atletas")
    private int minimoDeAtletas;

    @Column(name = "max_atletas")
    private int maximoDeAtletas;

    @OneToMany(mappedBy = "esporte",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Eventos> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "esporte",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Equipes> equipes = new ArrayList<>();


}
