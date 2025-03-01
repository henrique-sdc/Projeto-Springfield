package com.springfield.springfield_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CAD_USUARIO")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Caso queira associar com um cidadão:
    private Integer idCidadao;

    private String username;

    private String senha; // armazene o hash da senha

    private int tentativasFalhas;

    private LocalDateTime ultimoLogin;

    private boolean bloqueado;
}