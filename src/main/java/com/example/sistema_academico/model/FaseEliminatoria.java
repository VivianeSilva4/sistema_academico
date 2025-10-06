package com.example.sistema_academico.model;


import com.example.sistema_academico.domain.Fase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fase_eliminatoria")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class FaseEliminatoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fase")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Fase tipo;

    @ManyToOne
    @JoinColumn(name = "fk_evento")
    private Eventos evento;

    @ManyToOne
    @JoinColumn(name = "fk_jogo")
    private Jogo jogo;
}
