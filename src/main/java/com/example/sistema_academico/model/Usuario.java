package com.example.sistema_academico.model;

import com.example.sistema_academico.model.role.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;


@Entity
@Table(name = "usuarios")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Usuario  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Column(name="nome_completo")
    private String nomeCompleto;

    @Column(name="apelido")
    private String apelido;

    @Column(name= "telefone")
    private String telefone;

    @Column(name="matricula", unique = true)
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_usuario")
    private Role tipoUsuario;

    @Column(name="email", unique = true)
    @Email
    private String email;

    @Column(name="password")
    private String senha;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="data_criacao")
    private Date dataCriacao;

    @Column(name="ativo", nullable = false)
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name ="fk_curso")
    private Cursos curso;

    @OneToMany(mappedBy="arbitro", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Jogo> jogo = new ArrayList<>();

    @OneToMany(mappedBy = "tecnicoDaEquipe", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Equipes> equipeDaEquipe = new ArrayList<>();

    @ManyToMany(mappedBy = "atletasPorEquipe")
    private Collection<Equipes> equipesPorEquipe ;


}
