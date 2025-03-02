package com.springfield.springfield_api.service;

import com.springfield.springfield_api.model.Cidadao;
import com.springfield.springfield_api.model.Usuario;
import com.springfield.springfield_api.repository.CidadaoRepository;
import com.springfield.springfield_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CidadaoRepository cidadaoRepository; 

    public UsuarioService(UsuarioRepository usuarioRepository, CidadaoRepository cidadaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cidadaoRepository = cidadaoRepository;
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        Optional<Cidadao> cidadao = cidadaoRepository.findById(usuario.getIdCidadao());
        if (cidadao.isEmpty()) {
            throw new RuntimeException("Cidadão com ID " + usuario.getIdCidadao() + " não encontrado.");
        }

        if (usuarioRepository.existsByIdCidadao(usuario.getIdCidadao())) { 
            throw new RuntimeException("Já existe um usuário cadastrado para esse cidadão.");
        }

        usuario.setSenha(usuario.getSenha());
        usuario.setTentativasLogin(0);
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
            usuario.setTentativasLogin(usuario.getTentativasLogin() + 1);
            if (usuario.getTentativasLogin() >= 3) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
            throw new RuntimeException("Senha incorreta.");
        }



        if (usuario.getUltimoLogin() != null && usuario.getUltimoLogin().isBefore(LocalDateTime.now().minusDays(30))) {
        }

        usuario.setTentativasLogin(0);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return usuario;
    }


    public Usuario trocarSenha(Integer id, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }
    
        Usuario usuario = usuarioOpt.get();
    
        usuario.setSenha(novaSenha);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);
        return usuario;
    }


    public Usuario desbloquearUsuario(Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setTentativasLogin(0);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);

        return usuario;
    }
}