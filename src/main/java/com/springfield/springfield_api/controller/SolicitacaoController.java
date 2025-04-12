package com.springfield.springfield_api.controller;

import com.springfield.springfield_api.model.SolicitacaoHistorico;
import com.springfield.springfield_api.service.SolicitacaoService;
import com.springfield.springfield_api.statemachine.SolicitacaoEstado;
import com.springfield.springfield_api.statemachine.SolicitacaoEvento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Solicitações", description = "API para Gerenciamento do Ciclo de Vida de Solicitações de Cidadãos")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    @Operation(summary = "Inicia uma nova solicitação para um cidadão", description = "Cria o registro inicial da solicitação no estado SOLICITADO e retorna o ID único da demanda.")
    @ApiResponse(responseCode = "201", description = "Solicitação iniciada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição (ex: Cidadão não encontrado)")
    public ResponseEntity<?> iniciarSolicitacao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da nova solicitação", required = true) @RequestBody Map<String, String> request) {
        try {
            Integer cidadaoId = Integer.parseInt(request.get("cidadaoId"));
            String descricao = request.get("descricao");

            if (descricao == null || descricao.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "A descrição da demanda é obrigatória."));
            }

            UUID demandaId = solicitacaoService.iniciarSolicitacao(cidadaoId, descricao);
            log.info("Nova solicitação iniciada com ID: {}", demandaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("demandaId", demandaId));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "ID do cidadão inválido."));
        } catch (RuntimeException e) {
            log.error("Erro ao iniciar solicitação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{demandaId}/eventos")
    @Operation(summary = "Processa um evento para uma solicitação existente", description = "Muda o estado da solicitação com base no evento enviado (ANALISAR ou CONCLUIR).")
    @ApiResponse(responseCode = "200", description = "Evento processado e estado atualizado")
    @ApiResponse(responseCode = "400", description = "Erro na requisição (ex: Evento inválido para estado atual, Demanda não encontrada)")
    public ResponseEntity<?> processarEvento(
            @Parameter(description = "ID único da demanda (UUID)", required = true) @PathVariable UUID demandaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Evento a ser processado ('ANALISAR' ou 'CONCLUIR')", required = true) @RequestBody Map<String, String> request) {
        try {
            String eventoStr = request.get("evento");
            if (eventoStr == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "O campo 'evento' é obrigatório."));
            }

            SolicitacaoEvento evento = SolicitacaoEvento.valueOf(eventoStr.toUpperCase());
            SolicitacaoEstado novoEstado = solicitacaoService.processarEvento(demandaId, evento);

            log.info("Evento {} processado para demanda ID {}. Novo estado: {}", evento, demandaId, novoEstado);
            return ResponseEntity.ok(Map.of(
                    "demandaId", demandaId,
                    "eventoProcessado", evento,
                    "novoEstado", novoEstado));
        } catch (IllegalArgumentException e) {
            log.error("Evento inválido recebido: {}", request.get("evento"), e);
            return ResponseEntity.badRequest().body(Map.of("erro", "Evento inválido. Use 'ANALISAR' ou 'CONCLUIR'."));
        } catch (RuntimeException e) {
            log.error("Erro ao processar evento para demanda {}: {}", demandaId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/cidadao/{cidadaoId}")
    @Operation(summary = "Busca o histórico de solicitações de um cidadão", description = "Retorna todos os registros de mudança de estado para as solicitações de um cidadão específico.")
    @ApiResponse(responseCode = "200", description = "Histórico encontrado")
    @ApiResponse(responseCode = "404", description = "Cidadão não encontrado")
    public ResponseEntity<?> buscarHistoricoPorCidadao(
            @Parameter(description = "ID do cidadão", required = true) @PathVariable Integer cidadaoId) {
        try {
            List<SolicitacaoHistorico> historico = solicitacaoService.buscarHistoricoPorCidadao(cidadaoId);
            log.info("Histórico encontrado para cidadão {}: {} registros.", cidadaoId, historico.size());
            if (historico.isEmpty()) {
                return ResponseEntity.ok(historico);
            }
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            log.error("Erro ao buscar histórico para cidadão {}: {}", cidadaoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}