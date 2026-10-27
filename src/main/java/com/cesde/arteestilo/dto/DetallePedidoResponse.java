package com.cesde.arteestilo.dto;

import java.math.BigDecimal;

public record DetallePedidoResponse(
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
