package com.example.aula10.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PagamentoAdapterFactory {

    private final Map<String, PagamentoAdapter> adapterMap = new HashMap<>();

    @Autowired
    public PagamentoAdapterFactory(List<PagamentoAdapter> adapters) {
        for (PagamentoAdapter adapter : adapters) {
            String key = extrairMetodo(adapter);
            adapterMap.put(key, adapter);
        }
    }

    public PagamentoAdapter getAdapter(String metodoPagamento) {
        PagamentoAdapter adapter = adapterMap.get(metodoPagamento.toLowerCase());

        if (adapter == null) {
            throw new IllegalArgumentException("Método de pagamento não suportado: " + metodoPagamento);
        }

        return adapter;
    }

    private String extrairMetodo(PagamentoAdapter adapter) {
        String nome = adapter.getClass().getSimpleName().toLowerCase();
        if (nome.contains("pix")) return "pix";
        if (nome.contains("paypal")) return "paypal";
        if (nome.contains("boleto")) return "boleto";
        return nome;
    }
}