package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Usuario;
import com.example.sistema_academico.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario,Integer> {
    List<Usuario> findAllByTipoUsuario(Role tipo);

}
