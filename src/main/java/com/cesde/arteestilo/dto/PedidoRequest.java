package com.cesde.arteestilo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
        @NotNull Long usuarioId,
        @NotNull @Valid DireccionEnvioDTO direccionEnvio,
        @NotEmpty @Valid List<DetallePedidoRequest> detalles
) {}
