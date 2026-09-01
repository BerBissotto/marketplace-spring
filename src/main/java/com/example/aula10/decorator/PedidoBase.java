package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public class PedidoBase extends PedidoDecorator {

    public PedidoBase(Pedido pedido) {
        super(pedido);
    }

    @Override
    public double calcularCusto() {
        return pedido.calcularValorProdutos();
    }
}
