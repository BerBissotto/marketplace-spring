package com.example.aula10.controller;

import com.example.aula10.model.Produto;
import com.example.aula10.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/produtos")
public class ProdutoClienteController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/{id}")
    public String mostrarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        model.addAttribute("produto", produto);
        return "produtos/produtoLoja";
    }

    @GetMapping
    public String listarProdutos(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "valorMin", required = false) Double valorMin,
            @RequestParam(name = "valorMax", required = false) Double valorMax,
            @RequestParam(name = "categoria", required = false) String categoria,
            @PageableDefault(size = 1) Pageable pageable, // tamanho 1 para testes, ajuste se quiser
            Model model
    ) {
        Page<Produto> pagina;

        // Se nenhum filtro for informado, buscar todos com paginação
        if (nome == null && valorMin == null && valorMax == null && categoria == null) {
            pagina = produtoService.listarProdutos(pageable);
        } else {
            // Busca com filtros e paginação
            pagina = produtoService.buscarComFiltros(nome, valorMin, valorMax, categoria, pageable);
        }

        model.addAttribute("pagina", pagina);
        model.addAttribute("produtos", pagina.getContent());

        // Repassa filtros para o formulário para manter os valores na interface
        model.addAttribute("nome", nome);
        model.addAttribute("valorMin", valorMin);
        model.addAttribute("valorMax", valorMax);
        model.addAttribute("categoria", categoria);

        return "produtos/produtosgeral";
    }
}