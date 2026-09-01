package com.example.aula10.adapter;

import org.springframework.stereotype.Component;

@Component
public class PixAdapter implements PagamentoAdapter {

    @Override
    public boolean processarPagamento(double valor) {
        System.out.println("Processando pagamento via PIX...");
        System.out.printf("Valor: R$ %.2f%n", valor);

        System.out.println("Pagamento via PIX realizado com sucesso.");

        return true;
    }
}