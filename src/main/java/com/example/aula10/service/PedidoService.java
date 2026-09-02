package com.example.aula10.service;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
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

    public Optional<Pedido> buscarPorIdEUsuario(Long id, Usuario usuario) {
        return pedidoRepository.findByIdAndUsuario(id, usuario);
    }

    public Pedido salvar(Pedido pedido) {
        pedido.setValorTotal(pedido.calcularValorTotalDecorado());
        return pedidoRepository.save(pedido);
    }

    public Pedido salvarAdministrativo(Pedido dados, List<Produto> produtos, Usuario usuario) {
        Pedido pedido = dados.getId() == null
                ? new Pedido()
                : pedidoRepository.findById(dados.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        pedido.setDataPedido(dados.getDataPedido());
        pedido.setProdutos(produtos);
        pedido.setUsuario(usuario);
        pedido.setTipoFrete(dados.getTipoFrete());
        pedido.setTipoEmbalagem(dados.getTipoEmbalagem());
        pedido.setPercentualDesconto(dados.getPercentualDesconto());
        return salvar(pedido);
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
