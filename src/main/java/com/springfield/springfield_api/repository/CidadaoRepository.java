package com.springfield.springfield_api.repository;

import com.springfield.springfield_api.model.Cidadao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadaoRepository extends JpaRepository<Cidadao, Integer> {
}
