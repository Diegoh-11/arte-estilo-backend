package com.cesde.arte_estilo.controller;

import com.cesde.arte_estilo.model.EstadoPedido;
import com.cesde.arte_estilo.model.Pedido;
import com.cesde.arte_estilo.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * POST /api/v1/pedidos
     * Usa @RequestBody para recibir el pedido completo con sus detalles.
     * Aplica Regla 1 (stock) y Regla 2 (descuento).
     */
    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {
        Pedido creado = pedidoService.crearPedido(pedido);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    /**
     * POST /api/v1/pedidos/{id}/cancelar
     * Usa @PathVariable. Aplica Regla 3.
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }

    /**
     * GET /api/v1/pedidos?usuarioId=1&estado=PENDIENTE
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) EstadoPedido estado) {

        if (usuarioId != null && estado != null) {
            return ResponseEntity.ok(pedidoService.listarPorUsuarioYEstado(usuarioId, estado));
        }
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }
}
