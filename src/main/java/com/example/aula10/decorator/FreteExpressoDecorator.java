package com.example.aula10.decorator;


import com.example.aula10.model.Pedido;

public class FreteExpressoDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public FreteExpressoDecorator(PedidoDecorator decorado) {
        super(decorado.pedido);
        this.decorado = decorado;
    }

    @Override
    public double calcularCusto() {
        System.out.println("Aplicando frete expresso: +25.0");
        return decorado.calcularCusto() + 25.0;
    }
}