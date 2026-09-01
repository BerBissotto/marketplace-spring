package com.example.aula10.controller;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
import com.example.aula10.service.PedidoService;
import com.example.aula10.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pedidosadmin")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/form")
    public String novoPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "pedidos/form";
    }

    @GetMapping("/lista")
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        return "pedidos/lista";
    }


    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public String salvarPedido(@ModelAttribute Pedido pedido, @RequestParam("produtos") List<Long> produtoIds) {
        List<Produto> produtos = produtoService.buscarPorIds(produtoIds);
        pedido.setProdutos(produtos);

        if (pedido.getPercentualDesconto() == null) {
            pedido.setPercentualDesconto(0.0);
        }
        pedidoService.salvar(pedido);
        return "redirect:/pedidosadmin/lista";
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id).orElse(new Pedido());
        model.addAttribute("pedido", pedido);
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "pedidos/form";
    }

    @PostMapping("/deletar/{id}")
    public String deletarPedido(@PathVariable Long id) {
        pedidoService.deletar(id);
        return "redirect:/pedidosadmin/lista";
    }
}
