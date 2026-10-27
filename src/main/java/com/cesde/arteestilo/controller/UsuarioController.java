package com.cesde.arteestilo.controller;

import com.cesde.arteestilo.dto.UsuarioPatch;
import com.cesde.arteestilo.dto.UsuarioRequest;
import com.cesde.arteestilo.dto.UsuarioResponse;
import com.cesde.arteestilo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios y sus perfiles")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios activos")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear un usuario (con perfil opcional)")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar un usuario completo")
    public ResponseEntity<UsuarioResponse> reemplazar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioRequest dto) {
        return ResponseEntity.ok(usuarioService.reemplazar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un usuario")
    public ResponseEntity<UsuarioResponse> actualizarParcial(@PathVariable Long id,
                                                             @Valid @RequestBody UsuarioPatch dto) {
        return ResponseEntity.ok(usuarioService.actualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (baja lógica) un usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT PERSONALIZADO: Búsqueda por correo exacto
    @GetMapping("/buscar")
    @Operation(summary = "[Personalizado] Buscar usuario por correo")
    public ResponseEntity<UsuarioResponse> buscarPorCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
    }
}
