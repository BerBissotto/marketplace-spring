package com.example.aula10.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoCalculoTest {

    @Test
    void devePersistirFreteEmbalagemEDescontoNoMesmoTotal() {
        Pedido pedido = pedidoComProdutoDe(100.0);
        pedido.setTipoFrete("padrao");
        pedido.setTipoEmbalagem("presente");
        pedido.setPercentualDesconto(10.0);

        assertEquals(20.0, pedido.calcularValorFrete(), 0.001);
        assertEquals(10.0, pedido.calcularValorEmbalagem(), 0.001);
        assertEquals(13.0, pedido.calcularValorDesconto(), 0.001);
        assertEquals(117.0, pedido.calcularValorTotalDecorado(), 0.001);
    }

    @Test
    void deveAplicarDescontoDeUmPorCento() {
        Pedido pedido = pedidoComProdutoDe(100.0);
        pedido.setPercentualDesconto(1.0);

        assertEquals(99.0, pedido.calcularValorTotalDecorado(), 0.001);
    }

    @Test
    void semEmbalagemNaoDeveSubtrairValorDoPedido() {
        Pedido pedido = pedidoComProdutoDe(100.0);
        pedido.setTipoEmbalagem("semembalagem");

        assertEquals(0.0, pedido.calcularValorEmbalagem(), 0.001);
        assertEquals(100.0, pedido.calcularValorTotalDecorado(), 0.001);
    }

    private Pedido pedidoComProdutoDe(double preco) {
        Produto produto = new Produto();
        produto.setPreco(preco);

        Pedido pedido = new Pedido();
        pedido.setProdutos(List.of(produto));
        return pedido;
    }
}
