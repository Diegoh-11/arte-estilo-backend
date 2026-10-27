package com.cesde.arteestilo.dto;

import com.cesde.arteestilo.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        EstadoPedido estado,
        BigDecimal totalCompra,
        DireccionEnvioDTO direccionEnvio,
        List<DetallePedidoResponse> detalles,
        LocalDateTime fechaCreacion
) {}
