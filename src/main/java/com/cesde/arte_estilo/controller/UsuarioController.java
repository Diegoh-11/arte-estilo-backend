package com.cesde.arte_estilo.controller;

import com.cesde.arte_estilo.model.Usuario;
import com.cesde.arte_estilo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/v1/usuarios/{id}
     * Usa @PathVariable. Responde 200 o 404 (vía GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
}
