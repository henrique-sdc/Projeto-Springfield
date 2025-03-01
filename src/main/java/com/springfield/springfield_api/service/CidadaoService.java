package com.springfield.springfield_api.service;

import com.springfield.springfield_api.model.Cidadao;
import com.springfield.springfield_api.repository.CidadaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CidadaoService {

    private final CidadaoRepository cidadaoRepository;

    public CidadaoService(CidadaoRepository cidadaoRepository) {
        this.cidadaoRepository = cidadaoRepository;
    }

    public List<Cidadao> listarTodos() {
        return cidadaoRepository.findAll();
    }

    public Optional<Cidadao> buscarPorId(Integer id) {
        return cidadaoRepository.findById(id);
    }

    public Cidadao salvar(Cidadao cidadao) {
        return cidadaoRepository.save(cidadao);
    }

    public void deletar(Integer id) {
        cidadaoRepository.deleteById(id);
    }
}

