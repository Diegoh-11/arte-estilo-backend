package com.cesde.arteestilo.service;

import com.cesde.arteestilo.dto.CategoriaPatch;
import com.cesde.arteestilo.dto.CategoriaRequest;
import com.cesde.arteestilo.dto.CategoriaResponse;
import com.cesde.arteestilo.exception.ConflictoException;
import com.cesde.arteestilo.exception.RecursoNoEncontradoException;
import com.cesde.arteestilo.model.Categoria;
import com.cesde.arteestilo.repository.CategoriaRepository;
import com.cesde.arteestilo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findByEstadoActivoTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest dto) {
        String nombre = dto.nombre().trim();
        validarNombreLibre(nombre, null);
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(dto.descripcion());
        return toResponse(categoriaRepository.save(c));
    }

    /** PUT: reemplaza todos los campos editables. */
    @Transactional
    public CategoriaResponse reemplazar(Long id, CategoriaRequest dto) {
        Categoria c = buscar(id);
        String nombre = dto.nombre().trim();
        validarNombreLibre(nombre, id);
        c.setNombre(nombre);
        c.setDescripcion(dto.descripcion());
        return toResponse(categoriaRepository.save(c));
    }

    /** PATCH: cambia solo los campos que llegan en el JSON. */
    @Transactional
    public CategoriaResponse actualizarParcial(Long id, CategoriaPatch dto) {
        Categoria c = buscar(id);
        if (dto.nombre() != null) {
            String nombre = dto.nombre().trim();
            validarNombreLibre(nombre, id);
            c.setNombre(nombre);
        }
        if (dto.descripcion() != null) {
            c.setDescripcion(dto.descripcion());
        }
        return toResponse(categoriaRepository.save(c));
    }

    /** DELETE lógico: marca estadoActivo=false. No se permite si tiene productos activos. */
    @Transactional
    public void eliminar(Long id) {
        Categoria c = buscar(id);
        if (productoRepository.existsByCategoriaIdAndEstadoActivoTrue(id)) {
            throw new ConflictoException("No se puede eliminar la categoría: tiene productos activos");
        }
        c.setEstadoActivo(false);
        categoriaRepository.save(c);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getEstadoActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id " + id));
    }

    private void validarNombreLibre(String nombre, Long idActual) {
        boolean existe = (idActual == null)
                ? categoriaRepository.existsByNombre(nombre)
                : categoriaRepository.existsByNombreAndIdNot(nombre, idActual);
        if (existe) {
            throw new ConflictoException("Ya existe una categoría con el nombre '" + nombre + "'");
        }
    }

    private CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
