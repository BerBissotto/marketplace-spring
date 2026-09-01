package com.example.aula10.controller;

import com.example.aula10.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProdutoService produtoService;

    public HomeController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "index";
    }
}
