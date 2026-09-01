package com.example.aula10.adapter;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class BoletoAdapter implements PagamentoAdapter {

    @Override
    public boolean processarPagamento(double valor) {
        System.out.println("Gerando boleto bancário...");
        String codigoBarras = gerarCodigoBarras();
        LocalDate vencimento = LocalDate.now().plusDays(3);

        System.out.printf("Valor do boleto: R$ %.2f%n", valor);
        System.out.println("Código de barras: " + codigoBarras);
        System.out.println("Data de vencimento: " + vencimento);
        System.out.println("Boleto gerado com sucesso. Aguardando pagamento...");

        return true;
    }

    private String gerarCodigoBarras() {
        return UUID.randomUUID().toString().substring(0, 12).replace("-", "").toUpperCase();
    }
}