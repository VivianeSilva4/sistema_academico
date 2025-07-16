package com.example.sistema_academico.repository;

import com.example.sistema_academico.model.ClassificacaoGrupo;
import com.example.sistema_academico.model.Eventos;
import com.example.sistema_academico.model.FaseEliminatoria;
import com.example.sistema_academico.model.role.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFaseEliminatoriaRepository extends JpaRepository<FaseEliminatoria, Integer> {
    @Modifying
    @Query("DELETE FROM FaseEliminatoria " +
            "fe WHERE fe.evento.id = :eventoId AND fe.jogo.fase IN :fases")
    void deleteByEventoIdAndJogoFaseIn(@Param("eventoId") Integer eventoId,
                                       @Param("fases") List<Fase> fases);

    List<FaseEliminatoria> findByEvento(Eventos evento);
}
