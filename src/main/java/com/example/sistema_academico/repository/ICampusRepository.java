package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICampusRepository extends JpaRepository<Campus, Integer> {
     boolean existsByNomeAndEndereco(String nome, String endereco);
}
