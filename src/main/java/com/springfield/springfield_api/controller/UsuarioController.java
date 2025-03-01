package com.springfield.springfield_api.controller;

import com.springfield.springfield_api.model.Usuario;
import com.springfield.springfield_api.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.cadastrarUsuario(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> realizarLogin(@RequestBody Map<String, String> credenciais) {
        try {
            Usuario usuario = usuarioService.realizarLogin(credenciais.get("username"), credenciais.get("senha"));
            return ResponseEntity.ok(Map.of("mensagem", "Login realizado com sucesso!", "usuario", usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/trocar-senha")
    public ResponseEntity<?> trocarSenha(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Usuario usuario = usuarioService.trocarSenha(id, request.get("novaSenha"));
            return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!", "usuario", usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/desbloquear")
    public ResponseEntity<?> desbloquearUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.desbloquearUsuario(id);
            return ResponseEntity.ok(Map.of("mensagem", "Usuário desbloqueado com sucesso!", "usuario", usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}