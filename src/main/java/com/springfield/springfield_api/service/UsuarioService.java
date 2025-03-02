package com.springfield.springfield_api.service;

import com.springfield.springfield_api.model.Usuario;
import com.springfield.springfield_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByIdCidadao(usuario.getIdCidadao())) { 
            throw new RuntimeException("Já existe um usuário cadastrado para esse cidadão.");
        }

        usuario.setSenha(usuario.getSenha());
        usuario.setTentativasFalhas(0);
        usuario.setBloqueado(false);
        usuario.setUltimoLogin(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

     public Usuario realizarLogin(String username, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.isBloqueado()) {
            throw new RuntimeException("Usuário bloqueado. Solicite o desbloqueio.");
        }


        if (!usuario.getSenha().equals(senha)) {
            usuario.setTentativasFalhas(usuario.getTentativasFalhas() + 1);
            if (usuario.getTentativasFalhas() >= 3) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
            throw new RuntimeException("Senha incorreta.");
        }



        if (usuario.getUltimoLogin() != null && usuario.getUltimoLogin().isBefore(LocalDateTime.now().minusDays(30))) {
        }

        usuario.setTentativasFalhas(0);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return usuario;
    }


    public Usuario trocarSenha(Long id, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getUltimoLogin() != null && usuario.getUltimoLogin().isBefore(LocalDateTime.now().minusDays(30))) {
              usuario.setSenha(novaSenha);
              usuario.setUltimoLogin(LocalDateTime.now());
              usuarioRepository.save(usuario);
              return usuario;
        } else{
          throw new RuntimeException("A troca de senha não é obrigatória agora.");
        }
    }


    public Usuario desbloquearUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setTentativasFalhas(0);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);

        return usuario;
    }
}