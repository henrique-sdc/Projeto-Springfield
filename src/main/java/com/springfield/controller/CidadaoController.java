package com.springfield.controller;

import com.springfield.springfield_api.model.Cidadao;
import com.springfield.service.CidadaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cidadaos")
public class CidadaoController {

    private final CidadaoService cidadaoService;

    public CidadaoController(CidadaoService cidadaoService) {
        this.cidadaoService = cidadaoService;
    }

    @GetMapping
    public List<Cidadao> listarTodos() {
        return cidadaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cidadao> buscarPorId(@PathVariable Integer id) {
        Optional<Cidadao> cidadao = cidadaoService.buscarPorId(id);
        return cidadao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cidadao salvar(@RequestBody Cidadao cidadao) {
        return cidadaoService.salvar(cidadao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cidadao> atualizar(@PathVariable Integer id, @RequestBody Cidadao cidadao) {
        if (!cidadaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        cidadao.setId(id);
        return ResponseEntity.ok(cidadaoService.salvar(cidadao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!cidadaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        cidadaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

