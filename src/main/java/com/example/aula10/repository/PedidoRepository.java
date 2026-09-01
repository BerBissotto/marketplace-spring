package com.example.aula10.repository;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByUsuarioAndStatus(Usuario usuario, Pedido.StatusPedido status);
}
