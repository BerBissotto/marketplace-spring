package com.example.aula10.controller;

import com.example.aula10.model.Pagamento;
import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
import com.example.aula10.service.PagamentoService;
import com.example.aula10.service.PedidoService;
import com.example.aula10.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoClienteController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/finalizar")
    public String finalizarPagamento(@RequestParam("pedidoId") Long pedidoId,
                                     @RequestParam("formaPagamento") String metodoPagamento,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Pedido pedido = pedidoService.buscarPorId(pedidoId)
                .orElse(null);

        if (pedido == null || pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Pedido inválido ou carrinho vazio.");
            return "redirect:/pedidos";
        }

        // Recalcula no servidor imediatamente antes da cobrança e persiste o mesmo total.
        pedido = pedidoService.salvar(pedido);
        double valorTotal = pedido.getValorTotal();

        // Criar e salvar pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setMetodoPagamento(metodoPagamento);
        pagamento.setValor(valorTotal);
        pagamento.setPedido(pedido);
        pagamentoService.salvar(pagamento);

        // Atualizar estoque
        for (Produto produto : pedido.getProdutos()) {
            produto.setQuantidade(produto.getQuantidade() - 1);
            produtoService.salvar(produto);
        }

        // Atualizar pedido
        pedido.setStatus(Pedido.StatusPedido.FINALIZADO);
        pedido.setPagamento(pagamento);
        pedidoService.salvar(pedido);

        session.removeAttribute("pedidoAtual");

        redirectAttributes.addFlashAttribute("mensagem", "Pagamento concluído com sucesso!");
        return "redirect:/produtos";
    }

}
