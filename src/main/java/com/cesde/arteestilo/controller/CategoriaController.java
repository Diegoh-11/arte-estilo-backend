package com.cesde.arteestilo.controller;

import com.cesde.arteestilo.dto.CategoriaPatch;
import com.cesde.arteestilo.dto.CategoriaRequest;
import com.cesde.arteestilo.dto.CategoriaResponse;
import com.cesde.arteestilo.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar categorías activas")
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría por id")
    public ResponseEntity<CategoriaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear una categoría")
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar una categoría completa")
    public ResponseEntity<CategoriaResponse> reemplazar(@PathVariable Long id,
                                                        @Valid @RequestBody CategoriaRequest dto) {
        return ResponseEntity.ok(categoriaService.reemplazar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente una categoría")
    public ResponseEntity<CategoriaResponse> actualizarParcial(@PathVariable Long id,
                                                               @Valid @RequestBody CategoriaPatch dto) {
        return ResponseEntity.ok(categoriaService.actualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (baja lógica) una categoría sin productos activos")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
