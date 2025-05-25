package com.springfield.springfield_api.kafka;

import com.springfield.springfield_api.model.MensagemPrefeitura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ProdutorDeAvisos {

    private static final Logger logger = LoggerFactory.getLogger(ProdutorDeAvisos.class);

    private final KafkaTemplate<String, MensagemPrefeitura> kafkaTemplate;

    @Value("${springfield.kafka.topic.comunicados}")
    private String nomeDoTopico;

    public ProdutorDeAvisos(KafkaTemplate<String, MensagemPrefeitura> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarAviso(MensagemPrefeitura aviso) {
        try {
            aviso.setDataEnvio(LocalDateTime.now());
            kafkaTemplate.send(nomeDoTopico, aviso);
            logger.info("Aviso enviado ao topico '{}': {}", nomeDoTopico, aviso);
        } catch (Exception e) {
            logger.error("Falha ao enviar aviso ao topico '{}': {}", nomeDoTopico, aviso, e);
        }
    }
}