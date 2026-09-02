package com.example.aula10.controller;

import com.example.aula10.model.Pedido;
import com.example.aula10.model.Produto;
import com.example.aula10.model.Usuario;
import com.example.aula10.service.PedidoService;
import com.example.aula10.service.ProdutoService;
import com.example.aula10.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PedidoClienteController {

    private static final Set<String> FRETES_PERMITIDOS = Set.of("economico", "padrao", "expresso");
    private static final Set<String> EMBALAGENS_PERMITIDAS = Set.of("semembalagem", "basica", "presente", "comemorativa");

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private Pedido getPedidoPendenteOuNovo(Usuario usuario) {
        return pedidoService.buscarPedidoPendentePorUsuario(usuario)
                .orElseGet(() -> {
                    Pedido novoPedido = new Pedido();
                    novoPedido.setUsuario(usuario);
                    novoPedido.setProdutos(new ArrayList<>());
                    novoPedido.setStatus(Pedido.StatusPedido.PENDENTE);
                    return pedidoService.salvar(novoPedido);
                });
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam("produtoId") Long produtoId,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {

        Produto produto = produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidade() <= 0) {
            redirectAttributes.addFlashAttribute("erro", "Produto esgotado!");
            return "redirect:/produtos";
        }

        Usuario usuario = getUsuarioLogado();
        Pedido pedido = getPedidoPendenteOuNovo(usuario);

        if (pedido.getProdutos() == null) {
            pedido.setProdutos(new ArrayList<>());
        }

        long quantidadeNoCarrinho = pedido.getProdutos().stream()
                .filter(item -> Objects.equals(item.getId(), produtoId))
                .count();
        if (quantidadeNoCarrinho >= produto.getQuantidade()) {
            redirectAttributes.addFlashAttribute("erro", "Não há mais unidades disponíveis deste produto.");
            return "redirect:/produtos";
        }

        pedido.getProdutos().add(produto);
        pedido.setValorTotal(pedido.getProdutos().stream().mapToDouble(Produto::getPreco).sum());

        pedidoService.salvar(pedido);
        session.setAttribute("pedidoAtual", pedido); // opcional: para manter na sessão

        redirectAttributes.addFlashAttribute("mensagem", "Produto adicionado ao carrinho com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/pedidos")
    public String exibirCarrinho(HttpSession session, Model model) {
        Usuario usuario = getUsuarioLogado();
        Pedido pedido = getPedidoPendenteOuNovo(usuario);

        session.setAttribute("pedidoAtual", pedido); // opcional

        Map<Produto, Long> produtosQuantidades = pedido.getProdutos().stream()
                .collect(Collectors.groupingBy(p -> p, LinkedHashMap::new, Collectors.counting()));

        model.addAttribute("pedido", pedido);
        model.addAttribute("produtosQuantidades", produtosQuantidades);
        return "pedidos/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removerProduto(@RequestParam("produtoId") Long produtoId) {
        Usuario usuario = getUsuarioLogado();
        Pedido pedido = getPedidoPendenteOuNovo(usuario);

        pedido.getProdutos().removeIf(p -> Objects.equals(p.getId(), produtoId));
        pedido.setValorTotal(pedido.getProdutos().stream().mapToDouble(Produto::getPreco).sum());
        pedidoService.salvar(pedido);

        return "redirect:/pedidos";
    }

    @PostMapping("/carrinho/atualizar")
    public String atualizarQuantidade(@RequestParam("produtoId") Long produtoId,
                                      @RequestParam("quantidade") int quantidade,
                                      RedirectAttributes redirectAttributes) {
        if (quantidade <= 0) {
            redirectAttributes.addFlashAttribute("erro", "A quantidade deve ser maior que zero.");
            return "redirect:/pedidos";
        }

        Usuario usuario = getUsuarioLogado();
        Pedido pedido = getPedidoPendenteOuNovo(usuario);

        pedido.getProdutos().removeIf(p -> Objects.equals(p.getId(), produtoId));

        Produto produto = produtoService.buscarPorId(produtoId).orElse(null);
        if (produto != null) {
            if (quantidade > produto.getQuantidade()) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade solicitada maior que o estoque disponível.");
                return "redirect:/pedidos";
            }
            for (int i = 0; i < quantidade; i++) {
                pedido.getProdutos().add(produto);
            }
        }

        pedido.setValorTotal(pedido.getProdutos().stream().mapToDouble(Produto::getPreco).sum());
        pedidoService.salvar(pedido);

        return "redirect:/pedidos";
    }

    @PostMapping("/carrinho/finalizar")
    public String redirecionarParaPagamento(
            @RequestParam("frete") String tipoFrete,
            @RequestParam("embalagem") String tipoEmbalagem,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Usuario usuario = getUsuarioLogado();
        Pedido pedido = getPedidoPendenteOuNovo(usuario);

        tipoFrete = tipoFrete == null ? "" : tipoFrete.trim().toLowerCase(Locale.ROOT);
        tipoEmbalagem = tipoEmbalagem == null ? "" : tipoEmbalagem.trim().toLowerCase(Locale.ROOT);
        if (!FRETES_PERMITIDOS.contains(tipoFrete) || !EMBALAGENS_PERMITIDAS.contains(tipoEmbalagem)) {
            redirectAttributes.addFlashAttribute("erro", "Frete ou embalagem inválidos.");
            return "redirect:/pedidos";
        }

        if (pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Seu carrinho está vazio!");
            return "redirect:/pedidos";
        }

        // Salva as escolhas no pedido
        pedido.setTipoFrete(tipoFrete);
        pedido.setTipoEmbalagem(tipoEmbalagem);
        pedido = pedidoService.salvar(pedido);

        // Usa o mesmo valor calculado e persistido que será cobrado no pagamento.
        double subtotal = pedido.calcularValorProdutos();
        double frete = pedido.calcularValorFrete();
        double embalagem = pedido.calcularValorEmbalagem();
        double desconto = pedido.calcularValorDesconto();
        double total = pedido.getValorTotal();

        session.setAttribute("pedidoAtual", pedido);
        model.addAttribute("pedido", pedido);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("frete", frete);
        model.addAttribute("embalagem", embalagem);
        model.addAttribute("desconto", desconto);
        model.addAttribute("total", total);

        return "pagamentos/pagamento";
    }


}
