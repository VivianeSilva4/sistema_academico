package com.example.sistema_academico.model;

import com.example.sistema_academico.domain.Grau;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Entity
@Table(name = "eventos")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Eventos {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @ToString.Include
   @EqualsAndHashCode.Include
   @Column(name = "id_eventos")
   private Integer id;

   private String nome;

   @Temporal(TemporalType.TIMESTAMP)
   private Date data;

   @Enumerated(EnumType.STRING)
   private Grau nivel;

   @ManyToOne
   @JoinColumn(name = "fk_esporte")
   private Esportes esporte;

   @OneToMany(mappedBy = "evento",cascade = CascadeType.ALL,
           fetch = FetchType.LAZY, orphanRemoval = true)
   private List<Jogo> jogos = new ArrayList<>();

   @OneToMany(mappedBy = "evento",cascade = CascadeType.ALL,
           fetch = FetchType.LAZY, orphanRemoval = true)
   private List<Grupo> grupo = new ArrayList<>();



}
