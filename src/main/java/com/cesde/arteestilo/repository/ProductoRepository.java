package com.cesde.arteestilo.repository;

import com.cesde.arteestilo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Query Method derivado: busca productos activos por nombre de categoría. */
    List<Producto> findByCategoriaNombreAndEstadoActivoTrue(String nombreCategoria);

    /** JPQL explícita: productos activos con stock por encima de un mínimo. */
    @Query("SELECT p FROM Producto p WHERE p.stock > :stockMinimo AND p.estadoActivo = true")
    List<Producto> findProductosEnOfertaConStockSup(@Param("stockMinimo") Integer stockMinimo);

    List<Producto> findByEstadoActivoTrue();

    /** Endpoint personalizado: productos activos con poco inventario, de menor a mayor stock. */
    List<Producto> findByStockLessThanEqualAndEstadoActivoTrueOrderByStockAsc(Integer limite);

    boolean existsByCategoriaIdAndEstadoActivoTrue(Long categoriaId);
}
