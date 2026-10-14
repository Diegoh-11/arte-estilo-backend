package com.cesde.arteestilo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class Producto extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    /** Muchos productos pertenecen a una categoría. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /** Lado inverso de la relación M:N de favoritos (dueño: Usuario). */
    @Builder.Default
    @ManyToMany(mappedBy = "favoritos", fetch = FetchType.LAZY)
    private List<Usuario> usuariosQueFavorecen = new ArrayList<>();
}