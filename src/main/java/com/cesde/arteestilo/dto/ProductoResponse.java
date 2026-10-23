package com.cesde.arteestilo.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String imagenUrl,
        Long categoriaId,
        String categoriaNombre
) {}
