package com.cesde.arte_estilo.controller;

import com.cesde.arte_estilo.model.Producto;
import com.cesde.arte_estilo.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * GET /api/v1/productos?categoria=Sillas&minStock=5
     * Uso de @RequestParam para filtrar por categoría y/o stock mínimo.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer minStock) {

        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
        }
        if (minStock != null) {
            return ResponseEntity.ok(productoService.listarConStockSuperior(minStock));
        }
        // Fallback: listar todos con minStock 0
        return ResponseEntity.ok(productoService.listarConStockSuperior(0));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto creado = productoService.guardar(producto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }
}
