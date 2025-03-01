package com.springfield.springfield_api.controller;

import com.springfield.springfield_api.model.Cidadao;
import com.springfield.springfield_api.service.CidadaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
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

    @CrossOrigin(origins = "", allowedHeaders = "")
    @GetMapping("/{id}")
    public ResponseEntity<Cidadao> buscarPorId(@PathVariable Integer id) {
        Optional<Cidadao> cidadao = cidadaoService.buscarPorId(id);
        return cidadao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cidadao salvar(@RequestBody Cidadao cidadao) {
        return cidadaoService.salvar(cidadao);
    }

    @CrossOrigin(origins = "", allowedHeaders = "")
    @PutMapping("/{id}")
    public ResponseEntity<Cidadao> atualizar(@PathVariable Integer id, @RequestBody Cidadao cidadao) {
        if (!cidadaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        cidadao.setId(id);
        return ResponseEntity.ok(cidadaoService.salvar(cidadao));
    }

    @CrossOrigin(origins = "", allowedHeaders = "")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!cidadaoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        cidadaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

