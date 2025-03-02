package com.springfield.springfield_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CAD_CIDADAO")
@Data
public class Cidadao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String endereco;
    private String bairro;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { // <-- Adicionado este método!
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}