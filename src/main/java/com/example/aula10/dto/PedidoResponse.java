package com.example.aula10.dto;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;

import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
        Long id,
        LocalDate dataPedido,
        double valorTotal,
        String status,
        Long usuarioId,
        List<Long> produtoIds,
        Long pagamentoId,
        String tipoFrete,
        String tipoEmbalagem,
        Double percentualDesconto
) {
    public static PedidoResponse from(Pedido pedido) {
        List<Long> produtos = pedido.getProdutos() == null
                ? List.of()
                : pedido.getProdutos().stream().map(Produto::getId).toList();
        return new PedidoResponse(
                pedido.getId(),
                pedido.getDataPedido(),
                pedido.getValorTotal(),
                pedido.getStatus() != null ? pedido.getStatus().name() : null,
                pedido.getUsuario() != null ? pedido.getUsuario().getId() : null,
                produtos,
                pedido.getPagamento() != null ? pedido.getPagamento().getId() : null,
                pedido.getTipoFrete(),
                pedido.getTipoEmbalagem(),
                pedido.getPercentualDesconto()
        );
    }
}
