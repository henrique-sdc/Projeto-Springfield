package com.springfield.springfield_api.statemachine;

import com.springfield.springfield_api.model.SolicitacaoHistorico;
import com.springfield.springfield_api.repository.SolicitacaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalvarHistoricoAction implements Action<SolicitacaoEstado, SolicitacaoEvento> {

    private final SolicitacaoHistoricoRepository historicoRepository;

    @Override
    public void execute(StateContext<SolicitacaoEstado, SolicitacaoEvento> context) {

        UUID demandaId = Optional
                .ofNullable(context.getExtendedState().getVariables().get(StateMachineListener.DEMANDA_ID_HEADER))
                .map(o -> (UUID) o)
                .orElse(null);
        Integer cidadaoId = Optional
                .ofNullable(context.getExtendedState().getVariables().get(StateMachineListener.CIDADAO_ID_HEADER))
                .map(o -> (Integer) o)
                .orElse(null);
        String descricao = Optional
                .ofNullable(context.getExtendedState().getVariables().get(StateMachineListener.DESCRICAO_HEADER))
                .map(Object::toString)
                .orElse(null);
        SolicitacaoEstado estadoAlvo = context.getTarget().getId();
        SolicitacaoEvento evento = context.getEvent();

        log.info(">>> Action executing: Salvar Historico para estado {}, evento {}", estadoAlvo, evento);
        log.debug(">>> Action context variables: demandaId={}, cidadaoId={}, descricao={}", demandaId, cidadaoId,
                descricao);

        if (demandaId != null && cidadaoId != null && descricao != null) {
            log.info(">>> Action: Condition met. Attempting to save history...");

            SolicitacaoHistorico historico = new SolicitacaoHistorico();
            historico.setDemandaId(demandaId);
            historico.setCidadaoId(cidadaoId);
            historico.setDescricaoDemanda(descricao);
            historico.setEstado(estadoAlvo);
            historico.setEvento(evento);
            historico.setDataRegistro(LocalDateTime.now());

            try {
                SolicitacaoHistorico saved = historicoRepository.save(historico);
                log.info(">>> Action: History saved successfully. ID: {}", saved.getId());
            } catch (Exception e) {
                log.error(">>> Action: Failed to save SolicitacaoHistorico for demandaId: {}", demandaId, e);
                context.getStateMachine()
                        .setStateMachineError(new RuntimeException("Falha ao salvar histórico na action", e));
                throw new RuntimeException("Falha ao salvar histórico na action", e);

            }
        } else {
            log.warn(">>> Action: Condition NOT met (Missing context variables). History NOT saved for state {}.",
                    estadoAlvo);
            context.getStateMachine().setStateMachineError(
                    new RuntimeException("Variáveis de contexto ausentes na Action de salvar histórico"));
            throw new RuntimeException("Variáveis de contexto ausentes na Action de salvar histórico");
        }
    }
}