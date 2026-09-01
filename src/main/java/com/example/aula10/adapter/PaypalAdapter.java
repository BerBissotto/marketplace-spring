package com.example.aula10.adapter;

import org.springframework.stereotype.Component;

@Component
public class PaypalAdapter implements PagamentoAdapter {

    @Override
    public boolean processarPagamento(double valor) {
        System.out.println("Conectando ao serviço do PayPal...");
        System.out.printf("Valor: R$ %.2f%n", valor);

        System.out.println("Autenticando usuário PayPal...");
        System.out.println("Verificando saldo...");

        System.out.println("Pagamento via PayPal realizado com sucesso!");

        return true;
    }
}