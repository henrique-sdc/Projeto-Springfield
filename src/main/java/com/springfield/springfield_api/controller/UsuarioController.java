package com.springfield.springfield_api.controller;

import com.springfield.springfield_api.model.Usuario;
import com.springfield.springfield_api.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*") 
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage())); 
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> realizarLogin(@RequestBody Map<String, String> credenciais) {
        try {
            Usuario usuario = usuarioService.realizarLogin(credenciais.get("username"), credenciais.get("senha"));

             if (usuario.getUltimoLogin() != null && usuario.getUltimoLogin().isBefore(LocalDateTime.now().minusDays(30))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Troca de senha obrigatória."));
            }


            return ResponseEntity.ok(Map.of("mensagem", "Login realizado com sucesso!", "usuario", usuario));
        } catch (RuntimeException e) {
             if (e.getMessage().equals("Usuário bloqueado. Solicite o desbloqueio.")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
            }
        }
    }

    @PutMapping("/{id}/trocar-senha")
      public ResponseEntity<?> trocarSenha(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {

            if (request.get("novaSenha") == null || request.get("novaSenha").isEmpty()) {
              return ResponseEntity.badRequest().body(Map.of("erro", "A nova senha não pode ser vazia."));
            }

            Usuario usuario = usuarioService.trocarSenha(id, request.get("novaSenha"));
            return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!", "usuario", usuario));
        } catch (RuntimeException e) {
              if(e.getMessage().equals("A troca de senha não é obrigatória agora.")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage())); 
              }
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