package com.example.sistema_academico.repository;

import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.model.Campus;
import com.example.sistema_academico.model.Cursos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICursoRepository extends JpaRepository<Cursos, Integer> {
    boolean existsByNomeAndCampus(String nome, Campus campus);
}
