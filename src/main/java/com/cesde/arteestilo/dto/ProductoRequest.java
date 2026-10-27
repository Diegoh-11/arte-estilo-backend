package com.cesde.arteestilo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal precio,
        @NotNull @PositiveOrZero Integer stock,
        @Size(max = 255) String imagenUrl,
        @NotNull Long categoriaId
) {}
