package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.Grupo;
import com.example.sistema_academico.model.Jogo;
import com.example.sistema_academico.model.role.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IJogoRepository extends JpaRepository<Jogo,Integer> {

    List<Jogo> findByGrupoAndFase(Grupo grupo, Fase fase);

    @Modifying
    @Query("DELETE FROM Jogo j WHERE j.grupo.evento.id = :eventoId AND j.fase IN :fases")
    void deleteJogosByEventoAndFases(@Param("eventoId") Integer eventoId,
                                     @Param("fases") List<Fase> fases);

    List<Jogo> findByGrupoEventoIdAndFaseIn(Integer eventoId, List<Fase> fases);
}
