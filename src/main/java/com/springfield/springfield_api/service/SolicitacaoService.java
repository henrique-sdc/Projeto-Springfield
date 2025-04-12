package com.springfield.springfield_api.service;

import com.springfield.springfield_api.model.SolicitacaoHistorico;
import com.springfield.springfield_api.repository.CidadaoRepository;
import com.springfield.springfield_api.repository.SolicitacaoHistoricoRepository;
import com.springfield.springfield_api.statemachine.SolicitacaoEstado;
import com.springfield.springfield_api.statemachine.SolicitacaoEvento;
import com.springfield.springfield_api.statemachine.StateMachineListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final StateMachineFactory<SolicitacaoEstado, SolicitacaoEvento> stateMachineFactory;
    private final SolicitacaoHistoricoRepository historicoRepository;
    private final CidadaoRepository cidadaoRepository;

    @Transactional
    @SuppressWarnings("deprecation")
    public UUID iniciarSolicitacao(Integer cidadaoId, String descricao) {
        cidadaoRepository.findById(cidadaoId)
                .orElseThrow(() -> new RuntimeException("Cidadão com ID " + cidadaoId + " não encontrado."));

        UUID demandaId = UUID.randomUUID();

        StateMachine<SolicitacaoEstado, SolicitacaoEvento> sm = stateMachineFactory
                .getStateMachine(demandaId.toString());

        sm.stop();

        sm.getExtendedState().getVariables().put(StateMachineListener.DEMANDA_ID_HEADER, demandaId);
        sm.getExtendedState().getVariables().put(StateMachineListener.CIDADAO_ID_HEADER, cidadaoId);
        sm.getExtendedState().getVariables().put(StateMachineListener.DESCRICAO_HEADER, descricao);
        sm.getExtendedState().getVariables().remove(StateMachineListener.EVENTO_HEADER);

        sm.start();

        return demandaId;
    }

    @Transactional
    @SuppressWarnings("deprecation")
    public SolicitacaoEstado processarEvento(UUID demandaId, SolicitacaoEvento evento) {
        SolicitacaoHistorico ultimoHistorico = historicoRepository
                .findFirstByDemandaIdOrderByDataRegistroDesc(demandaId)
                .orElseThrow(() -> new RuntimeException("Demanda com ID " + demandaId + " não encontrada."));

        SolicitacaoEstado estadoAtual = ultimoHistorico.getEstado();
        Integer cidadaoId = ultimoHistorico.getCidadaoId();
        String descricao = ultimoHistorico.getDescricaoDemanda();

        StateMachine<SolicitacaoEstado, SolicitacaoEvento> sm = stateMachineFactory
                .getStateMachine(demandaId.toString());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.resetStateMachine(new DefaultStateMachineContext<SolicitacaoEstado, SolicitacaoEvento>(
                            estadoAtual, null, null, null, null, sm.getId()));
                });

        sm.getExtendedState().getVariables().put(StateMachineListener.DEMANDA_ID_HEADER, demandaId);
        sm.getExtendedState().getVariables().put(StateMachineListener.CIDADAO_ID_HEADER, cidadaoId);
        sm.getExtendedState().getVariables().put(StateMachineListener.DESCRICAO_HEADER, descricao);
        sm.getExtendedState().getVariables().put(StateMachineListener.EVENTO_HEADER, evento);

        sm.start();

        Message<SolicitacaoEvento> message = MessageBuilder
                .withPayload(evento)
                .build();

        boolean eventAccepted = sm.sendEvent(message);

        if (!eventAccepted) {
            sm.stop();
            throw new RuntimeException("Evento " + evento + " não foi aceito pela State Machine para demanda "
                    + demandaId + " no estado " + estadoAtual);
        }

        SolicitacaoEstado estadoFinal = sm.getState().getId();

        sm.stop();

        return estadoFinal;
    }

    public List<SolicitacaoHistorico> buscarHistoricoPorCidadao(Integer cidadaoId) {
        cidadaoRepository.findById(cidadaoId)
                .orElseThrow(() -> new RuntimeException("Cidadão com ID " + cidadaoId + " não encontrado."));

        return historicoRepository.findByCidadaoIdOrderByDataRegistroDesc(cidadaoId);
    }
}