package com.cesde.arte_estilo.repository;

import com.cesde.arte_estilo.model.EstadoPedido;
import com.cesde.arte_estilo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /** Query Method derivado: pedidos de un usuario filtrados por estado. */
    List<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);
}
