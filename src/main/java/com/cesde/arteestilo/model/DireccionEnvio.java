package com.cesde.arteestilo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DireccionEnvio {

    @Column(name = "direccion", length = 150, nullable = false)
    private String direccion;

    @Column(name = "ciudad", length = 50)
    private String ciudad;

    @Column(name = "departamento", length = 50)
    private String departamento;

    @Column(name = "referencia", length = 100)
    private String referencia;
}
