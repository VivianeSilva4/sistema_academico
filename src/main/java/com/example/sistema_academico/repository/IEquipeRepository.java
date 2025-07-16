package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEquipeRepository extends JpaRepository<Equipes,Integer> {

    boolean existsByEsporteAndCurso(Esportes esportes, Cursos curso);
    Optional<Equipes> findFirstByTecnicoDaEquipe(Usuario tecnicoDaEquipe);

}
