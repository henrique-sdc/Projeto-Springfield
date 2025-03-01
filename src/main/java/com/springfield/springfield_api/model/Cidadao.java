package com.springfield.springfield_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CAD_CIDADAO")
@Data
public class Cidadao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // O ID é gerenciado pelo próprio banco de dados

    private String nome;
    private String endereco;
    private String bairro;
}

