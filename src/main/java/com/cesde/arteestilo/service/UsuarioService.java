package com.cesde.arteestilo.service;

import com.cesde.arteestilo.dto.UsuarioPatch;
import com.cesde.arteestilo.dto.UsuarioRequest;
import com.cesde.arteestilo.dto.UsuarioResponse;
import com.cesde.arteestilo.exception.ConflictoException;
import com.cesde.arteestilo.exception.RecursoNoEncontradoException;
import com.cesde.arteestilo.model.PerfilUsuario;
import com.cesde.arteestilo.model.Usuario;
import com.cesde.arteestilo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findByEstadoActivoTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest dto) {
        validarCorreoLibre(dto.correo(), null);
        Usuario u = new Usuario();
        u.setNombre(dto.nombre().trim());
        u.setCorreo(dto.correo());
        u.setTelefono(dto.telefono());
        aplicarPerfil(u, dto.biografia(), dto.fotoPerfilUrl(), true);
        return toResponse(usuarioRepository.save(u));
    }

    /** PUT: reemplaza todos los campos editables (incluido el perfil). */
    @Transactional
    public UsuarioResponse reemplazar(Long id, UsuarioRequest dto) {
        Usuario u = buscar(id);
        validarCorreoLibre(dto.correo(), id);
        u.setNombre(dto.nombre().trim());
        u.setCorreo(dto.correo());
        u.setTelefono(dto.telefono());
        aplicarPerfil(u, dto.biografia(), dto.fotoPerfilUrl(), true);
        return toResponse(usuarioRepository.save(u));
    }

    /** PATCH: cambia solo los campos que llegan en el JSON. */
    @Transactional
    public UsuarioResponse actualizarParcial(Long id, UsuarioPatch dto) {
        Usuario u = buscar(id);
        if (dto.correo() != null) {
            validarCorreoLibre(dto.correo(), id);
            u.setCorreo(dto.correo());
        }
        if (dto.nombre() != null) u.setNombre(dto.nombre().trim());
        if (dto.telefono() != null) u.setTelefono(dto.telefono());
        aplicarPerfil(u, dto.biografia(), dto.fotoPerfilUrl(), false);
        return toResponse(usuarioRepository.save(u));
    }

    /** DELETE lógico: marca estadoActivo=false (sus pedidos se conservan). */
    @Transactional
    public void eliminar(Long id) {
        Usuario u = buscar(id);
        u.setEstadoActivo(false);
        usuarioRepository.save(u);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .filter(u -> Boolean.TRUE.equals(u.getEstadoActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con el correo: " + correo));
        return toResponse(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .filter(u -> Boolean.TRUE.equals(u.getEstadoActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id " + id));
    }

    private void validarCorreoLibre(String correo, Long idActual) {
        boolean ocupado = usuarioRepository.findByCorreo(correo)
                .filter(otro -> idActual == null || !otro.getId().equals(idActual))
                .isPresent();
        if (ocupado) {
            throw new ConflictoException("Ya existe un usuario con el correo " + correo);
        }
    }

    /**
     * reemplazar=true  -> los valores null limpian el campo (PUT / crear).
     * reemplazar=false -> los valores null se ignoran (PATCH).
     */
    private void aplicarPerfil(Usuario u, String biografia, String foto, boolean reemplazar) {
        PerfilUsuario perfil = u.getPerfilUsuario();
        if (perfil == null) {
            if (biografia == null && foto == null) {
                return;
            }
            perfil = new PerfilUsuario();
            perfil.setUsuario(u);
            u.setPerfilUsuario(perfil);
        }
        if (reemplazar || biografia != null) perfil.setBiografia(biografia);
        if (reemplazar || foto != null) perfil.setFotoPerfilUrl(foto);
    }

    private UsuarioResponse toResponse(Usuario u) {
        PerfilUsuario perfil = u.getPerfilUsuario();
        return new UsuarioResponse(
                u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(),
                perfil != null ? perfil.getBiografia() : null,
                perfil != null ? perfil.getFotoPerfilUrl() : null);
    }
}
