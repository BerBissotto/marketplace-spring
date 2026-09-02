package com.example.aula10.controller;

import org.springframework.ui.Model;
import com.example.aula10.model.Pagamento;
import com.example.aula10.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pagamentosadmin")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @InitBinder("pagamento")
    public void restringirCamposPagamento(WebDataBinder binder) {
        binder.setAllowedFields("id", "metodoPagamento", "pedido.id");
    }

    @GetMapping("/form")
    public String form(@RequestParam(required = false) Long id, Model model) {
        Pagamento pagamento = (id != null) ? pagamentoService.buscarPorId(id).orElse(new Pagamento()) : new Pagamento();
        model.addAttribute("pagamento", pagamento);
        return "pagamentos/form";
    }

    @PostMapping("/save")
    public String salvar(@ModelAttribute Pagamento pagamento) {
        pagamentoService.salvar(pagamento);
        return "redirect:/pagamentosadmin/lista";
    }

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("pagamentos", pagamentoService.listarPagamentos());
        return "pagamentos/lista";
    }

    @PostMapping("/delete/{id}")
    public String deletar(@PathVariable Long id) {
        pagamentoService.deletar(id);
        return "redirect:/pagamentosadmin/lista";
    }
}
