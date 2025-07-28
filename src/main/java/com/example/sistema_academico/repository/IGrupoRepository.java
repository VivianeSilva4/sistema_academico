package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGrupoRepository extends JpaRepository<Grupo,Integer> {
    List<Grupo> findByEvento(Eventos eventos);

    @Query("SELECT DISTINCT g FROM Grupo g JOIN FETCH g.equipe WHERE g.id = :id")
    Optional<Grupo> findByIdComEquipes(@Param("id") Integer id);

}
