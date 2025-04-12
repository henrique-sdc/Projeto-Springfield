package com.springfield.springfield_api.statemachine;

import com.springfield.springfield_api.model.SolicitacaoHistorico;
import com.springfield.springfield_api.repository.SolicitacaoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineUtils;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateMachineListener extends StateMachineListenerAdapter<SolicitacaoEstado, SolicitacaoEvento> {

    public static final String DEMANDA_ID_HEADER = "demandaId";
    public static final String CIDADAO_ID_HEADER = "cidadaoId";
    public static final String DESCRICAO_HEADER = "descricao";
    public static final String EVENTO_HEADER = "evento";

    @Override
    public void transitionEnded(Transition<SolicitacaoEstado, SolicitacaoEvento> transition) {
        if (transition != null && transition.getTarget() != null) {
            SolicitacaoEstado estadoAlvo = transition.getTarget().getId();
            SolicitacaoEvento evento = Optional.ofNullable(transition.getTrigger())
                    .map(trigger -> trigger.getEvent())
                    .orElse(null);
            log.info(">>> Listener: Transition ended. Target state: {}, Event: {}", estadoAlvo, evento);
        } else {
            log.warn(">>> Listener: transitionEnded called with null transition or target state.");
        }
    }

    @Override
    public void stateMachineError(StateMachine<SolicitacaoEstado, SolicitacaoEvento> stateMachine,
            Exception exception) {
        log.error(">>> Listener: StateMachine error!", exception);
    }

    @Override
    public void eventNotAccepted(Message<SolicitacaoEvento> event) {
        log.warn(">>> Listener: Event not accepted: {}", event.getPayload());
    }

    @Override
    public void stateEntered(State<SolicitacaoEstado, SolicitacaoEvento> state) {
        log.debug(">>> Listener: State entered: {}", state.getId());
    }

    @Override
    public void stateChanged(State<SolicitacaoEstado, SolicitacaoEvento> from,
            State<SolicitacaoEstado, SolicitacaoEvento> to) {
        log.debug(">>> Listener: State changed from {} to {}",
                Optional.ofNullable(from).map(State::getId).orElse(null),
                Optional.ofNullable(to).map(State::getId).orElse(null));
    }

}