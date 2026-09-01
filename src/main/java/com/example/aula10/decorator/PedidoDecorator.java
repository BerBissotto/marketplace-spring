package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public abstract class PedidoDecorator {
    protected Pedido pedido;

    public PedidoDecorator(Pedido pedido) {
        this.pedido = pedido;
    }

    // Método que retorna o custo total considerando o decorator
    public abstract double calcularCusto();
}
