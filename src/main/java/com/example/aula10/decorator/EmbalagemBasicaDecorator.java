package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public class EmbalagemBasicaDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public EmbalagemBasicaDecorator(PedidoDecorator decorado) {
        super(decorado.pedido);
        this.decorado = decorado;
    }

    @Override
    public double calcularCusto() {
        // Soma o custo da embalagem básica ao custo calculado pelo decorator encadeado
        return decorado.calcularCusto() + 1.5;
    }
}