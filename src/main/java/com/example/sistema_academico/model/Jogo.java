package com.example.sistema_academico.model;

import com.example.sistema_academico.domain.Fase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "jogo")
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jogo")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "placa_a")
    private Integer placaA;

    @Column(name ="placa_b")
    private Integer placaB;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "wo_a", nullable = false)
    private Boolean woA;

    @Column(name = "wo_b",nullable = false)
    private Boolean woB;

    private boolean finalizado;

    @Enumerated(EnumType.STRING)
    private Fase fase;

    @ManyToOne
    @JoinColumn(name="fk_arbitro")
    private Usuario arbitro;

    @ManyToOne
    @JoinColumn(name ="fk_equipe_A")
    private Equipes equipeA;

    @ManyToOne
    @JoinColumn(name = "fk_equipe_B")
    private Equipes equipeB;

    @ManyToOne
    @JoinColumn(name = "fk_evento")
    private Eventos evento;

    @ManyToOne
    @JoinColumn(name="fk_grupo")
    private Grupo grupo;

    public boolean isFinalizado() {
        return (placaA != null && placaB != null)
                || Boolean.TRUE.equals(woA)
                || Boolean.TRUE.equals(woB);
    }

}
