package com.springfield.springfield_api.kafka;

import com.springfield.springfield_api.model.MensagemPrefeitura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReceptorDeAvisos {

    private static final Logger logger = LoggerFactory.getLogger(ReceptorDeAvisos.class);
    private final List<MensagemPrefeitura> caixaDeEntrada = new CopyOnWriteArrayList<>();
    private static final int MAX_AVISOS_GUARDADOS = 50;

    @KafkaListener(topics = "${springfield.kafka.topic.comunicados}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactoryMensagemPrefeitura")
    public void escutarAvisos(MensagemPrefeitura aviso) {
        logger.info("Aviso recebido: {}", aviso);
        synchronized (caixaDeEntrada) {
            if (caixaDeEntrada.size() >= MAX_AVISOS_GUARDADOS) {
                caixaDeEntrada.remove(0);
            }
            caixaDeEntrada.add(aviso);
        }
    }

    public List<MensagemPrefeitura> getUltimosAvisosPublicados() {
        List<MensagemPrefeitura> copia = new ArrayList<>(this.caixaDeEntrada);
        Collections.reverse(copia);
        return Collections.unmodifiableList(copia);
    }
}