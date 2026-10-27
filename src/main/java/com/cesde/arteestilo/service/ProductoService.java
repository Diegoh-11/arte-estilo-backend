package com.cesde.arteestilo.service;

import com.cesde.arteestilo.dto.ProductoPatch;
import com.cesde.arteestilo.dto.ProductoRequest;
import com.cesde.arteestilo.dto.ProductoResponse;
import com.cesde.arteestilo.exception.RecursoNoEncontradoException;
import com.cesde.arteestilo.model.Categoria;
import com.cesde.arteestilo.model.Producto;
import com.cesde.arteestilo.repository.CategoriaRepository;
import com.cesde.arteestilo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String categoria, Integer minStock) {
        List<Producto> productos;
        if (categoria != null && !categoria.isBlank()) {
            productos = productoRepository.findByCategoriaNombreAndEstadoActivoTrue(categoria);
        } else if (minStock != null) {
            productos = productoRepository.findProductosEnOfertaConStockSup(minStock);
        } else {
            productos = productoRepository.findByEstadoActivoTrue();
        }
        return productos.stream().map(this::toResponse).toList();
    }

    /** ENDPOINT PERSONALIZADO 2: productos activos con stock menor o igual al límite. */
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarBajoStock(Integer limite) {
        if (limite == null || limite < 0) {
            throw new IllegalArgumentException("El límite debe ser un número mayor o igual a cero");
        }
        return productoRepository.findByStockLessThanEqualAndEstadoActivoTrueOrderByStockAsc(limite)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest dto) {
        Producto p = new Producto();
        p.setNombre(dto.nombre().trim());
        p.setPrecio(dto.precio());
        p.setStock(dto.stock());
        p.setImagenUrl(dto.imagenUrl());
        p.setCategoria(buscarCategoria(dto.categoriaId()));
        return toResponse(productoRepository.save(p));
    }

    /** PUT: reemplaza todos los campos editables. */
    @Transactional
    public ProductoResponse reemplazar(Long id, ProductoRequest dto) {
        Producto p = buscar(id);
        p.setNombre(dto.nombre().trim());
        p.setPrecio(dto.precio());
        p.setStock(dto.stock());
        p.setImagenUrl(dto.imagenUrl());
        p.setCategoria(buscarCategoria(dto.categoriaId()));
        return toResponse(productoRepository.save(p));
    }

    /** PATCH: cambia solo los campos que llegan en el JSON. */
    @Transactional
    public ProductoResponse actualizarParcial(Long id, ProductoPatch dto) {
        Producto p = buscar(id);
        if (dto.nombre() != null) p.setNombre(dto.nombre().trim());
        if (dto.precio() != null) p.setPrecio(dto.precio());
        if (dto.stock() != null) p.setStock(dto.stock());
        if (dto.imagenUrl() != null) p.setImagenUrl(dto.imagenUrl());
        if (dto.categoriaId() != null) p.setCategoria(buscarCategoria(dto.categoriaId()));
        return toResponse(productoRepository.save(p));
    }

    /** DELETE lógico: marca estadoActivo=false (los pedidos antiguos conservan su referencia). */
    @Transactional
    public void eliminar(Long id) {
        Producto p = buscar(id);
        p.setEstadoActivo(false);
        productoRepository.save(p);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .filter(p -> Boolean.TRUE.equals(p.getEstadoActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id " + id));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getEstadoActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id " + id));
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), p.getImagenUrl(),
                p.getCategoria().getId(), p.getCategoria().getNombre());
    }
}
