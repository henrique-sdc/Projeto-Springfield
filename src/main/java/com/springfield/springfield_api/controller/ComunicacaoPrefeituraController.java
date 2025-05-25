package com.springfield.springfield_api.controller;

import com.springfield.springfield_api.kafka.ProdutorDeAvisos;
import com.springfield.springfield_api.kafka.ReceptorDeAvisos;
import com.springfield.springfield_api.model.MensagemPrefeitura;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prefeitura/comunicacao")
@CrossOrigin(origins = "*")
@Tag(name = "Comunicação Prefeitura (Kafka)", description = "Endpoints para comunicados da prefeitura")
public class ComunicacaoPrefeituraController {

    private final ProdutorDeAvisos produtorAvisos;
    private final ReceptorDeAvisos receptorAvisos;

    public ComunicacaoPrefeituraController(ProdutorDeAvisos produtor, ReceptorDeAvisos receptor) {
        this.produtorAvisos = produtor;
        this.receptorAvisos = receptor;
    }

    @PostMapping("/publicar-aviso")
    @Operation(summary = "Prefeitura publica um novo aviso")
    public ResponseEntity<Map<String, String>> publicarAvisoPrefeitura(@RequestBody MensagemPrefeitura aviso) {
        if (aviso.getTitulo() == null || aviso.getTitulo().isBlank() ||
                aviso.getConteudo() == null || aviso.getConteudo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("status", "Erro: Titulo e conteudo sao obrigatorios."));
        }
        if (aviso.getSetorRemetente() == null || aviso.getSetorRemetente().isBlank()) {
            aviso.setSetorRemetente("Prefeitura Springfield");
        }
        produtorAvisos.enviarAviso(aviso);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("status", "Aviso encaminhado para publicacao."));
    }

    @GetMapping("/ler-avisos")
    @Operation(summary = "Cidadaos visualizam os ultimos avisos publicados")
    public ResponseEntity<List<MensagemPrefeitura>> lerAvisosCidadaos() {
        return ResponseEntity.ok(receptorAvisos.getUltimosAvisosPublicados());
    }
}