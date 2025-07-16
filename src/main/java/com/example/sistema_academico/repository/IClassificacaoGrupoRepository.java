package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.ClassificacaoGrupo;
import com.example.sistema_academico.model.Equipes;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IClassificacaoGrupoRepository extends JpaRepository<ClassificacaoGrupo, Integer> {
    void deleteByGrupo(Grupo grupo);

    List<ClassificacaoGrupo> findByGrupo(Grupo grupo);


    @Query("SELECT cg FROM ClassificacaoGrupo cg WHERE cg.equipe = :equipe" +
            " AND cg.grupo.evento = :evento")
    Optional<ClassificacaoGrupo> findByEquipeAndGrupoEvento
            (@Param("equipe") Equipes equipe, @Param("evento") Eventos evento);



}
