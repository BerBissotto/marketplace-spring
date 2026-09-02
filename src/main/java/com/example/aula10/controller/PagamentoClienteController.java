package com.example.aula10.controller;

import com.example.aula10.model.Usuario;
import com.example.aula10.service.CheckoutService;
import com.example.aula10.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoClienteController {

    private final CheckoutService checkoutService;
    private final UsuarioService usuarioService;

    public PagamentoClienteController(CheckoutService checkoutService, UsuarioService usuarioService) {
        this.checkoutService = checkoutService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/finalizar")
    public String finalizarPagamento(@RequestParam("formaPagamento") String metodoPagamento,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado."));

        try {
            checkoutService.finalizarPedidoPendente(usuario, metodoPagamento);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/pedidos";
        }

        session.removeAttribute("pedidoAtual");

        redirectAttributes.addFlashAttribute("mensagem", "Pagamento concluído com sucesso!");
        return "redirect:/produtos";
    }

}
