package com.springfield.springfield_api.repository;

import com.springfield.springfield_api.model.SolicitacaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitacaoHistoricoRepository extends JpaRepository<SolicitacaoHistorico, Long> {

    Optional<SolicitacaoHistorico> findFirstByDemandaIdOrderByDataRegistroDesc(UUID demandaId);

    List<SolicitacaoHistorico> findByCidadaoIdOrderByDataRegistroDesc(Integer cidadaoId);

    List<SolicitacaoHistorico> findByDemandaIdOrderByDataRegistroAsc(UUID demandaId);
}