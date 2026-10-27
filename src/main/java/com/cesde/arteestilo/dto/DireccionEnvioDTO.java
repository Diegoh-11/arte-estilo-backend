package com.cesde.arteestilo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionEnvioDTO(
        @NotBlank @Size(max = 150) String direccion,
        @Size(max = 50) String ciudad,
        @Size(max = 50) String departamento,
        @Size(max = 100) String referencia
) {}
