package com.cesde.arteestilo.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaPatch(
        @Pattern(regexp = ".*\\S.*", message = "no puede estar vacío") @Size(max = 50) String nombre,
        @Size(max = 255) String descripcion
) {}
