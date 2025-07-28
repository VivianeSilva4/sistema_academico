package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.ClassificacaoGrupo;
import com.example.sistema_academico.model.ClassificacaoGrupoId;
import com.example.sistema_academico.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IClassificacaoGrupoRepository extends JpaRepository<ClassificacaoGrupo, ClassificacaoGrupoId> {
    void deleteByGrupo(Grupo grupo);

    Optional<ClassificacaoGrupo> findByGrupoIdAndEquipeId(Integer grupoId, Integer equipeId);

    List<ClassificacaoGrupo> findByGrupo(Grupo grupo);
    @Query("""
    SELECT c FROM ClassificacaoGrupo c
    WHERE c.grupo.id = :grupoId
    ORDER BY c.pontos DESC, c.saldoGols DESC, c.vitorias DESC
""")
    List<ClassificacaoGrupo> buscarTop2PorGrupo(@Param("grupoId") Integer grupoId);

}
