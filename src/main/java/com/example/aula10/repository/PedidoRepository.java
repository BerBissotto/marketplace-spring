package com.example.aula10.repository;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByUsuarioAndStatus(Usuario usuario, Pedido.StatusPedido status);

    Optional<Pedido> findByIdAndUsuario(Long id, Usuario usuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario AND p.status = :status")
    Optional<Pedido> findByUsuarioAndStatusForUpdate(
            @Param("usuario") Usuario usuario,
            @Param("status") Pedido.StatusPedido status
    );
}
