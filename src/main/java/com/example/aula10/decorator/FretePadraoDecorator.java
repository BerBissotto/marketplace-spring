package com.example.aula10.decorator;

public class FretePadraoDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public FretePadraoDecorator(PedidoDecorator decorado){
        super(decorado.pedido);
        this.decorado = decorado;
        System.out.println("Frete padrão selecionado!");
        System.out.println("Prazo de Entrega de 10 à 15 dias.");
    }

    @Override
    public double calcularCusto(){
        return decorado.calcularCusto() + 20;
    }
}
