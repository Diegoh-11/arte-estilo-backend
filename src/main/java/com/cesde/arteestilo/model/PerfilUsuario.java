package com.cesde.arteestilo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "perfiles_usuario")
public class PerfilUsuario extends BaseEntity {

    @Column(length = 255)
    private String biografia;

    @Column(name = "foto_perfil_url", length = 255)
    private String fotoPerfilUrl;

    /** Lado inverso de la relación 1:1 con Usuario. */
    @OneToOne(mappedBy = "perfilUsuario")
    private Usuario usuario;
}