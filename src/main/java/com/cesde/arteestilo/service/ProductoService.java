package com.cesde.arteestilo.service;

import com.cesde.arteestilo.exception.RecursoNoEncontradoException;
import com.cesde.arteestilo.model.Producto;
import com.cesde.arteestilo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaNombreAndEstadoActivoTrue(categoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarConStockSuperior(Integer minStock) {
        return productoRepository.findProductosEnOfertaConStockSup(minStock);
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }

    @Transactional
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }
}