package com.cesde.arteestilo.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String telefono,
        String biografia,
        String fotoPerfilUrl
) {}
