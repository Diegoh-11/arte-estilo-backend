package com.cesde.arteestilo.service;

import com.cesde.arteestilo.exception.MontoInvalidoException;
import com.cesde.arteestilo.exception.PedidoNoCancelableException;
import com.cesde.arteestilo.exception.RecursoNoEncontradoException;
import com.cesde.arteestilo.exception.StockInsuficienteException;
import com.cesde.arteestilo.model.*;
import com.cesde.arteestilo.repository.PedidoRepository;
import com.cesde.arteestilo.repository.ProductoRepository;
import com.cesde.arteestilo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PedidoService {

    private static final BigDecimal MONTO_DESCUENTO = new BigDecimal("2000000");
    private static final BigDecimal PORCENTAJE_DESCUENTO = new BigDecimal("0.10");

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }
 /**
     * ═══════════════════════════════════════════════════════════════════
     * REGLA 1 — Validación y deducción de stock al crear un pedido
     * ───────────────────────────────────────────────────────────────────
     * Qué evalúa: para cada línea (DetallePedido) verifica que el stock
     *             del producto sea >= a la cantidad solicitada.
     * Por qué/cuándo lanza excepción: si NO hay stock suficiente lanza
     *             StockInsuficienteException → HTTP 400 Bad Request.
     * Qué hace al ser exitoso: descuenta la cantidad del stock de cada
     *             producto, recalcula el total y persiste el pedido con
     *             sus detalles (cascade ALL).
     * ═══════════════════════════════════════════════════════════════════
     */
 @Transactional
 public Pedido crearPedido(Pedido pedido) {
     if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
         throw new IllegalArgumentException("El pedido debe contener al menos un detalle");
     }

     BigDecimal totalCalculado = BigDecimal.ZERO;

     for (DetallePedido detalle : pedido.getDetalles()) {
         if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
             throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
         }

         Producto producto = productoRepository.findById(detalle.getProducto().getId())
                 .orElseThrow(() -> new RecursoNoEncontradoException(
                         "Producto no encontrado con id " + detalle.getProducto().getId()));

         // ---- Validación de stock (Regla 1) ----
         if (producto.getStock() < detalle.getCantidad()) {
             throw new StockInsuficienteException(
                     "Stock insuficiente para el producto '" + producto.getNombre() +
                     "'. Disponible: " + producto.getStock() +
                     ", solicitado: " + detalle.getCantidad());
         }
// Descuento de stock
producto.setStock(producto.getStock() - detalle.getCantidad());
productoRepository.save(producto);

// Vinculación bidireccional y cálculo del total
detalle.setProducto(producto);
detalle.setPedido(pedido);
detalle.setPrecioUnitario(producto.getPrecio());

totalCalculado = totalCalculado.add(
        producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
}

// ---- Regla 2: aplicar descuento si corresponde ----
BigDecimal totalFinal = aplicarDescuento(totalCalculado);

pedido.setTotalCompra(totalFinal);
if (pedido.getEstado() == null) {
pedido.setEstado(EstadoPedido.PENDIENTE);
}

// Verificar existencia del usuario antes de persistir
Usuario usuario = usuarioRepository.findById(pedido.getUsuario().getId())
    .orElseThrow(() -> new RecursoNoEncontradoException(
            "Usuario no encontrado con id " + pedido.getUsuario().getId()));
pedido.setUsuario(usuario);

return pedidoRepository.save(pedido);
}

/**
* ═══════════════════════════════════════════════════════════════════
* REGLA 2 — Aplicación de descuento por compra superior
* ───────────────────────────────────────────────────────────────────
* Qué evalúa: si el total supera los $2.000.000 COP.
* Por qué/cuándo lanza excepción: si el total es nulo o negativo
*             lanza MontoInvalidoException → HTTP 400 Bad Request.
* Qué hace al ser exitoso: si supera el monto aplica 10% de descuento
*             y retorna el nuevo total; si no, retorna el total sin
*             descuento.
* ═══════════════════════════════════════════════════════════════════
*/
private BigDecimal aplicarDescuento(BigDecimal total) {
    if (total == null || total.signum() < 0) {
        throw new MontoInvalidoException("El monto total del pedido no puede ser negativo");
    }

    if (total.compareTo(MONTO_DESCUENTO) > 0) {
        BigDecimal descuento = total.multiply(PORCENTAJE_DESCUENTO);
        return total.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
    }

    return total.setScale(2, RoundingMode.HALF_UP);
}

/**
 * ═══════════════════════════════════════════════════════════════════
 * REGLA 3 — Cancelación de pedido según estado
 * ───────────────────────────────────────────────────────────────────
 * Qué evalúa: el estado actual del pedido.
 * Por qué/cuándo lanza excepción: si está EN_CAMINO o ENTREGADO
 *             (o CANCELADO) lanza PedidoNoCancelableException →
 *             HTTP 400 Bad Request.
 * Qué hace al ser exitoso: cambia el estado a CANCELADO y reintegra
 *             el stock de cada producto del pedido.
 * ═══════════════════════════════════════════════════════════════════
 */
@Transactional
public Pedido cancelarPedido(Long pedidoId) {
    Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + pedidoId));
            if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
                throw new PedidoNoCancelableException(
                        "Solo se pueden cancelar pedidos en estado PENDIENTE. Estado actual: " + pedido.getEstado());
            }
    
            // Reintegrar stock
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
    
            pedido.setEstado(EstadoPedido.CANCELADO);
            return pedidoRepository.save(pedido);
        }
    
        @Transactional(readOnly = true)
        public List<Pedido> listarPorUsuarioYEstado(Long usuarioId, EstadoPedido estado) {
            return pedidoRepository.findByUsuarioIdAndEstado(usuarioId, estado);
        }
    
        @Transactional(readOnly = true)
        public Pedido obtenerPorId(Long id) {
            return pedidoRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id " + id));
        }
    
        @Transactional(readOnly = true)
        public List<Pedido> listarTodos() {
            return pedidoRepository.findAll();
        }
    }