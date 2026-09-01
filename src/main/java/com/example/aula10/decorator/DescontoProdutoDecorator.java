package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public class DescontoProdutoDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;
    private double percentualDesconto;

    public DescontoProdutoDecorator(PedidoDecorator decorado, double percentualDesconto) {
        super(decorado.pedido);
        this.decorado = decorado;
        this.percentualDesconto = percentualDesconto;
        System.out.println("Aplicando desconto de " + percentualDesconto + "%");
    }

    @Override
    public double calcularCusto() {
        double custoBase = decorado.calcularCusto();
        return custoBase * (1 - (percentualDesconto / 100.0));
    }
}