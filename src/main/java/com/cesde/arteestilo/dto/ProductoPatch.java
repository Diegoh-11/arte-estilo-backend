package com.cesde.arteestilo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductoPatch(
        @Pattern(regexp = ".*\\S.*", message = "no puede estar vacío") @Size(max = 100) String nombre,
        @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal precio,
        @PositiveOrZero Integer stock,
        @Size(max = 255) String imagenUrl,
        Long categoriaId
) {}
