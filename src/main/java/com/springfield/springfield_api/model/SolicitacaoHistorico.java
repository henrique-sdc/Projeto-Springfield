package com.springfield.springfield_api.model;

import com.springfield.springfield_api.statemachine.SolicitacaoEstado;
import com.springfield.springfield_api.statemachine.SolicitacaoEvento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SOLICITACAO_HISTORICO")
@Data
@NoArgsConstructor
public class SolicitacaoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "demanda_id", nullable = false)
    private UUID demandaId;

    @Column(name = "cidadao_id", nullable = false)
    private Integer cidadaoId;

    @Column(name = "descricao_demanda", nullable = false, length = 500)
    private String descricaoDemanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolicitacaoEstado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SolicitacaoEvento evento;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    public SolicitacaoHistorico(UUID demandaId, Integer cidadaoId, String descricaoDemanda, SolicitacaoEstado estado,
            SolicitacaoEvento evento) {
        this.demandaId = demandaId;
        this.cidadaoId = cidadaoId;
        this.descricaoDemanda = descricaoDemanda;
        this.estado = estado;
        this.evento = evento;
        this.dataRegistro = LocalDateTime.now();
    }
}