package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Eventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEventosRepository extends JpaRepository<Eventos,Integer> {
    Optional<Eventos> findByNomeIgnoreCase(String name);

}
