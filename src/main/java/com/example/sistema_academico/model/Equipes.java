package com.example.sistema_academico.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "equipes")
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Equipes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipe")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "fk_tecnico")
    private Usuario tecnicoDaEquipe;

    @ManyToMany
    @JoinTable(
            name = "equipe_atleta",
            joinColumns = {@JoinColumn(name = "fk_equipe")},
            inverseJoinColumns = {@JoinColumn( name ="fk_usuario")}
    )
    private Collection<Usuario> atletasPorEquipe;

    @OneToMany(mappedBy = "equipe",cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ClassificacaoGrupo> classificacaoGrupo = new ArrayList<>();

    @OneToMany(mappedBy = "equipeA", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Jogo> jogosDaEquipeA = new ArrayList<>();

    @OneToMany(mappedBy = "equipeB", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Jogo> jogosDaEquipeB = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fk_esporte")
    private Esportes esporte;

    @ManyToOne
    @JoinColumn(name = "fk_curso")
    private Cursos curso;

    @ManyToMany(mappedBy = "equipe")
    private Collection<Grupo> grupo;

}
