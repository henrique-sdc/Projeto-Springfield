package com.springfield.springfield_api.model;

import java.time.LocalDateTime;

public class MensagemPrefeitura {
    private String titulo;
    private String conteudo;
    private String setorRemetente;
    private LocalDateTime dataEnvio;

    public MensagemPrefeitura() {
    }

    public MensagemPrefeitura(String titulo, String conteudo, String setorRemetente, LocalDateTime dataEnvio) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.setorRemetente = setorRemetente;
        this.dataEnvio = dataEnvio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getSetorRemetente() {
        return setorRemetente;
    }

    public void setSetorRemetente(String setorRemetente) {
        this.setorRemetente = setorRemetente;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    @Override
    public String toString() {
        return "MensagemPrefeitura{" +
                "titulo='" + titulo + '\'' +
                ", conteudo='" + conteudo + '\'' +
                ", setorRemetente='" + setorRemetente + '\'' +
                ", dataEnvio=" + dataEnvio +
                '}';
    }
}