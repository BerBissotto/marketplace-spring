package com.example.aula10.repository;

import com.example.aula10.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Produto p WHERE p.id = :id")
    Optional<Produto> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM Produto p WHERE " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:valorMin IS NULL OR p.preco >= :valorMin) AND " +
            "(:valorMax IS NULL OR p.preco <= :valorMax) AND " +
            "(:categoria IS NULL OR LOWER(p.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))")
    List<Produto> buscarComFiltros(
            @Param("nome") String nome,
            @Param("valorMin") Double valorMin,
            @Param("valorMax") Double valorMax,
            @Param("categoria") String categoria
    );
    @Query("SELECT p FROM Produto p WHERE " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:valorMin IS NULL OR p.preco >= :valorMin) AND " +
            "(:valorMax IS NULL OR p.preco <= :valorMax) AND " +
            "(:categoria IS NULL OR LOWER(p.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))")
    Page<Produto> buscarComFiltros(
            @Param("nome") String nome,
            @Param("valorMin") Double valorMin,
            @Param("valorMax") Double valorMax,
            @Param("categoria") String categoria,
            Pageable pageable
    );
}
