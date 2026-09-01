package com.example.aula10.decorator;

public class FreteEconomicoDecorator extends PedidoDecorator {
    private PedidoDecorator decorado;

    public FreteEconomicoDecorator(PedidoDecorator decorado){
        super(decorado.pedido);
        this.decorado = decorado;
        System.out.println("Frete Econômico selecionado!");
        System.out.println("Prazo de Entrega de 15 à 30 dias.");
    }

    @Override
    public double calcularCusto(){
        return decorado.calcularCusto() + 10.0;
    }
}
