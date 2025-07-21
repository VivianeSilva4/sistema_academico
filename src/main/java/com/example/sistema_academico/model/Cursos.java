package com.example.sistema_academico.model;

import com.example.sistema_academico.domain.Grau;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "cursos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Cursos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cursos")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Grau nivel;

    @ManyToOne
    @JoinColumn(name = "fk_campus", nullable = false)
    private Campus campus;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private  List<Usuario> atleta = new ArrayList<>();

    @OneToMany(mappedBy = "curso",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Equipes> equipes = new ArrayList<>();


}
