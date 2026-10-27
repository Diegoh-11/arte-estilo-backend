package com.cesde.arteestilo.dto;

import com.cesde.arteestilo.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record PedidoEstadoPatch(@NotNull EstadoPedido estado) {}
