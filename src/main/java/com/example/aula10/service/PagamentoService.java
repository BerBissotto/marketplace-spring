package com.example.aula10.service;

import com.example.aula10.adapter.PagamentoAdapter;
import com.example.aula10.adapter.PagamentoAdapterFactory;
import com.example.aula10.model.Pagamento;
import com.example.aula10.repository.PagamentoRepository;
import com.example.aula10.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PagamentoAdapterFactory adapterFactory;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pagamento> listarPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> buscarPorId(Long id) {
        return pagamentoRepository.findById(id);
    }

    public Pagamento salvar(Pagamento pagamento) {
        if (pagamento.getMetodoPagamento() == null || pagamento.getPedido() == null) {
            throw new IllegalArgumentException("Método de pagamento e pedido não podem ser nulos.");
        }

        if (pagamento.getPedido().getId() == null) {
            throw new IllegalArgumentException("Pedido inválido.");
        }

        pagamento.setPedido(pedidoRepository.findById(pagamento.getPedido().getId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado.")));
        pagamento.setMetodoPagamento(pagamento.getMetodoPagamento().trim().toLowerCase());
        pagamento.setValor(pagamento.getPedido().getValorTotal());
        if (pagamento.getValor() <= 0) {
            throw new IllegalArgumentException("O pedido deve possuir um valor válido.");
        }

        PagamentoAdapter adapter = adapterFactory.getAdapter(pagamento.getMetodoPagamento());

        boolean sucesso = adapter.processarPagamento(pagamento.getValor());

        if (!sucesso) {
            throw new RuntimeException("Falha ao processar pagamento com método: " + pagamento.getMetodoPagamento());
        }

        return pagamentoRepository.save(pagamento);
    }

    public void deletar(Long id) {
        pagamentoRepository.deleteById(id);
    }

    public Pagamento atualizar(Long id, Pagamento novoPagamento) {
        return pagamentoRepository.findById(id).map(pagamento -> {
            pagamento.setMetodoPagamento(novoPagamento.getMetodoPagamento());
            pagamento.setPedido(novoPagamento.getPedido());
            return pagamentoRepository.save(pagamento);
        }).orElseGet(() -> {
            novoPagamento.setId(id);
            return pagamentoRepository.save(novoPagamento);
        });
    }
}
