package com.springfield.springfield_api.service;

import com.springfield.springfield_api.model.Usuario;
import com.springfield.springfield_api.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        // Verifica se já existe um usuário para o mesmo cidadão
        if (usuarioRepository.existsByIdCidadao(usuario.getIdCidadao())) {
            throw new RuntimeException("Já existe um usuário cadastrado para esse cidadão.");
        }

        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
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

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            usuario.setTentativasFalhas(usuario.getTentativasFalhas() + 1);
            if (usuario.getTentativasFalhas() >= 3) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
            throw new RuntimeException("Senha incorreta.");
        }

        // Se o usuário ficou mais de 30 dias sem login, força troca de senha
        if (usuario.getUltimoLogin() != null && usuario.getUltimoLogin().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new RuntimeException("Você precisa trocar sua senha antes de acessar.");
        }

        // Resetar tentativas e atualizar o último login
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
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setUltimoLogin(LocalDateTime.now()); // Atualiza o login para evitar novo bloqueio
        usuarioRepository.save(usuario);

        return usuario;
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
