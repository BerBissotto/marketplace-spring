package com.example.aula10.decorator;

import com.example.aula10.model.Pedido;

public class EmbalagemComemorativaDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public EmbalagemComemorativaDecorator(PedidoDecorator decorado){
        super(decorado.pedido);
        this.decorado = decorado;
    }

    @Override
    public double calcularCusto(){
        System.out.println("Realizando embalagem para data comemorativa!");
        return decorado.calcularCusto() + 12.0;
    }
}