package com.springfield.springfield_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CAD_USUARIO_CIDADAO")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CIDADAO_ID")
    private Integer idCidadao;

    private String username;
    private String senha;
    private int tentativasLogin;
    private LocalDateTime ultimoLogin;
    private boolean bloqueado;
}
