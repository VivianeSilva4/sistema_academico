package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Esportes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEsporteRepository extends JpaRepository<Esportes,Integer> {

    Optional<Esportes> findByNomeIgnoreCase(String name);
}
