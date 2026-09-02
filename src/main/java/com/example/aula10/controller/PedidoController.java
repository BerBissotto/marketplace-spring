package com.example.aula10.controller;

import com.example.aula10.dto.PedidoResponse;
import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
import com.example.aula10.service.UsuarioService;
import com.example.aula10.service.PedidoService;
import com.example.aula10.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
@RequestMapping("/pedidosadmin")
public class PedidoController {

    private static final Set<String> FRETES_PERMITIDOS = Set.of("", "economico", "padrao", "expresso");
    private static final Set<String> EMBALAGENS_PERMITIDAS = Set.of("", "semembalagem", "basica", "presente", "comemorativa");

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    @InitBinder("pedido")
    public void restringirCamposPedido(WebDataBinder binder) {
        binder.setAllowedFields("id", "dataPedido", "tipoFrete", "tipoEmbalagem", "percentualDesconto");
    }

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
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(PedidoResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public String salvarPedido(@ModelAttribute Pedido pedido,
                               @RequestParam("usuarioId") Long usuarioId,
                               @RequestParam("produtos") List<Long> produtoIds,
                               Model model) {
        List<Produto> produtos = produtoService.buscarPorIds(produtoIds);
        if (produtos.size() != produtoIds.stream().distinct().count()) {
            return formularioComErro(pedido, model, "Um ou mais produtos são inválidos.");
        }

        String frete = normalizar(pedido.getTipoFrete());
        String embalagem = normalizar(pedido.getTipoEmbalagem());
        Double desconto = pedido.getPercentualDesconto() == null ? 0.0 : pedido.getPercentualDesconto();
        if (pedido.getDataPedido() == null
                || !FRETES_PERMITIDOS.contains(frete)
                || !EMBALAGENS_PERMITIDAS.contains(embalagem)
                || desconto < 0 || desconto > 100) {
            return formularioComErro(pedido, model, "Confira a data, o frete, a embalagem e o desconto.");
        }

        try {
            pedido.setTipoFrete(frete);
            pedido.setTipoEmbalagem(embalagem);
            pedido.setPercentualDesconto(desconto);
            pedidoService.salvarAdministrativo(pedido, produtos, usuarioService.buscarPorId(usuarioId));
            return "redirect:/pedidosadmin/lista";
        } catch (IllegalArgumentException exception) {
            return formularioComErro(pedido, model, exception.getMessage());
        }
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

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private String formularioComErro(Pedido pedido, Model model, String mensagem) {
        model.addAttribute("pedido", pedido);
        model.addAttribute("produtos", produtoService.listarProdutos());
        model.addAttribute("erro", mensagem);
        return "pedidos/form";
    }
}
