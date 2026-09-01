package com.example.aula10.service;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Usuario;
import com.example.aula10.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido salvar(Pedido pedido) {
        pedido.setValorTotal(pedido.calcularValorTotalDecorado());
        return pedidoRepository.save(pedido);
    }

    public void deletar(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido atualizar(Long id, Pedido novoPedido) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setProdutos(novoPedido.getProdutos());
            pedido.setUsuario(novoPedido.getUsuario());
            pedido.setValorTotal(novoPedido.getValorTotal());
            return pedidoRepository.save(pedido);
        }).orElseGet(() -> {
            novoPedido.setId(id);
            return pedidoRepository.save(novoPedido);
        });
    }
    public Optional<Pedido> buscarPedidoPendentePorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioAndStatus(usuario, Pedido.StatusPedido.PENDENTE);
    }

}
