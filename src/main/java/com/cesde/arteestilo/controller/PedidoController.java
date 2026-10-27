package com.cesde.arteestilo.controller;

import com.cesde.arteestilo.dto.PedidoEstadoPatch;
import com.cesde.arteestilo.dto.PedidoRequest;
import com.cesde.arteestilo.dto.PedidoResponse;
import com.cesde.arteestilo.model.EstadoPedido;
import com.cesde.arteestilo.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Creación y ciclo de vida de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un pedido",
            description = "Aplica Regla 1 (stock) y Regla 2 (10% de descuento si el total supera $2.000.000)")
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(dto));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "[Personalizado] Cancelar un pedido",
            description = "Regla 3: solo pedidos PENDIENTES. Reintegra el stock de los productos.")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Avanzar el estado de un pedido",
            description = "Flujo permitido: PENDIENTE -> EN_CAMINO -> ENTREGADO")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable Long id,
                                                        @Valid @RequestBody PedidoEstadoPatch dto) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, dto));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos",
            description = "Si se envían usuarioId y estado juntos, filtra por ambos")
    public ResponseEntity<List<PedidoResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listar(usuarioId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por id")
    public ResponseEntity<PedidoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtener(id));
    }
}
