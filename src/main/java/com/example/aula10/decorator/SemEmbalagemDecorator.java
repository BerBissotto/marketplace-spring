package com.example.aula10.decorator;

public class SemEmbalagemDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public SemEmbalagemDecorator(PedidoDecorator decorado){
        super(decorado.pedido);
        this.decorado = decorado;
    }

    @Override
    public double calcularCusto() {
        System.out.println("Produto separado sem embalagem!");
        return decorado.calcularCusto();
    }
}
