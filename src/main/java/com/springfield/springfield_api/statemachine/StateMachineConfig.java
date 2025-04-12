package com.springfield.springfield_api.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<SolicitacaoEstado, SolicitacaoEvento> {

    private final StateMachineListener<SolicitacaoEstado, SolicitacaoEvento> listener;
    private final Action<SolicitacaoEstado, SolicitacaoEvento> salvarHistoricoAction;

    public StateMachineConfig(StateMachineListener<SolicitacaoEstado, SolicitacaoEvento> listener,
            Action<SolicitacaoEstado, SolicitacaoEvento> salvarHistoricoAction) {
        this.listener = listener;
        this.salvarHistoricoAction = salvarHistoricoAction;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<SolicitacaoEstado, SolicitacaoEvento> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(false)
                .listener(listener);
    }

    @Override
    public void configure(StateMachineStateConfigurer<SolicitacaoEstado, SolicitacaoEvento> states) throws Exception {
        states
                .withStates()
                .initial(SolicitacaoEstado.SOLICITADO)
                .stateEntry(SolicitacaoEstado.SOLICITADO, entryAction())

                .states(EnumSet.allOf(SolicitacaoEstado.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SolicitacaoEstado, SolicitacaoEvento> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(SolicitacaoEstado.SOLICITADO).target(SolicitacaoEstado.AGUARDANDO_ANALISE)
                .event(SolicitacaoEvento.ANALISAR)
                .action(salvarHistoricoAction)
                .and()
                .withExternal()
                .source(SolicitacaoEstado.AGUARDANDO_ANALISE).target(SolicitacaoEstado.CONCLUIDO)
                .event(SolicitacaoEvento.CONCLUIR)
                .action(salvarHistoricoAction);
    }

    @Bean
    public Action<SolicitacaoEstado, SolicitacaoEvento> entryAction() {
        log.info("Configuring entryAction bean...");
        return salvarHistoricoAction;
    }
}