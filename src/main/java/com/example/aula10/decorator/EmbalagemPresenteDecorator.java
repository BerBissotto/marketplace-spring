package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public class EmbalagemPresenteDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public EmbalagemPresenteDecorator(PedidoDecorator decorado){
        super(decorado.pedido);
        this.decorado = decorado;
    }

    @Override
    public double calcularCusto() {
        System.out.println("Realizando embalagem de presente!");
        return decorado.calcularCusto() + 10.0;
    }
}