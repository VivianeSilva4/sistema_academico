package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGrupoRepository extends JpaRepository<Grupo,Integer> {
    List<Grupo> findByEvento(Eventos eventos);
}
