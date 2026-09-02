package com.example.aula10.service;

import com.example.aula10.model.Pagamento;
import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
import com.example.aula10.model.Usuario;
import com.example.aula10.repository.PedidoRepository;
import com.example.aula10.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class CheckoutService {

    private static final Set<String> METODOS_PERMITIDOS = Set.of("pix", "paypal", "boleto");

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoService pedidoService;
    private final PagamentoService pagamentoService;

    public CheckoutService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository,
                           PedidoService pedidoService, PagamentoService pagamentoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoService = pedidoService;
        this.pagamentoService = pagamentoService;
    }

    @Transactional
    public Pedido finalizarPedidoPendente(Usuario usuario, String metodoPagamento) {
        String metodo = metodoPagamento == null ? "" : metodoPagamento.trim().toLowerCase(Locale.ROOT);
        if (!METODOS_PERMITIDOS.contains(metodo)) {
            throw new IllegalArgumentException("Forma de pagamento inválida.");
        }

        Pedido pedido = pedidoRepository
                .findByUsuarioAndStatusForUpdate(usuario, Pedido.StatusPedido.PENDENTE)
                .orElseThrow(() -> new IllegalArgumentException("Pedido pendente não encontrado."));

        if (pedido.getPagamento() != null) {
            throw new IllegalStateException("Este pedido já possui pagamento.");
        }
        if (pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("Pedido inválido ou carrinho vazio.");
        }

        Map<Long, Long> quantidades = new LinkedHashMap<>();
        pedido.getProdutos().forEach(produto ->
                quantidades.merge(produto.getId(), 1L, Long::sum));

        List<Produto> produtosAtualizados = new ArrayList<>();
        quantidades.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Produto produto = produtoRepository.findByIdForUpdate(entry.getKey())
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
                    long quantidadeSolicitada = entry.getValue();
                    if (produto.getQuantidade() < quantidadeSolicitada) {
                        throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
                    }
                    for (long i = 0; i < quantidadeSolicitada; i++) {
                        produtosAtualizados.add(produto);
                    }
                });

        pedido.setProdutos(produtosAtualizados);
        pedido = pedidoService.salvar(pedido);

        Pagamento pagamento = new Pagamento();
        pagamento.setMetodoPagamento(metodo);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setPedido(pedido);
        pagamento = pagamentoService.salvar(pagamento);

        quantidades.forEach((produtoId, quantidade) -> {
            Produto produto = produtosAtualizados.stream()
                    .filter(item -> item.getId().equals(produtoId))
                    .findFirst()
                    .orElseThrow();
            produto.setQuantidade(produto.getQuantidade() - quantidade.intValue());
            produtoRepository.save(produto);
        });

        pedido.setStatus(Pedido.StatusPedido.FINALIZADO);
        pedido.setPagamento(pagamento);
        return pedidoService.salvar(pedido);
    }
}
