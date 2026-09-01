package com.example.aula10.controller;

import com.example.aula10.factory.ProdutoFactory;
import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;
import com.example.aula10.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/produtosadmin")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("/form")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "produtos/lista";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/novo")
    public String salvarProdutoComFactory(@RequestParam String nome,
                                          @RequestParam int quantidade,
                                          @RequestParam String descricao,
                                          @RequestParam String categoria,
                                          @RequestParam double preco,
                                          @RequestParam(value = "imagens", required = false) List<MultipartFile> arquivos) throws IOException {

        Produto produto = ProdutoFactory.criarProduto(nome, quantidade, descricao, categoria, preco, arquivos);
        produtoService.salvar(produto);
        return "redirect:/produtosadmin";
    }

    @PostMapping("/atualizar")
    public String atualizarProduto(
            @RequestParam("id") Long id,
            @RequestParam("nome") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam("quantidade") int quantidade,
            @RequestParam("categoria") String categoria,
            @RequestParam("preco") double preco,
            @RequestParam(value = "imagens", required = false) List<MultipartFile> arquivos
    ) throws IOException {

        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Atualiza os campos básicos
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setQuantidade(quantidade);
        produto.setCategoria(categoria);
        produto.setPreco(preco);

        // Processa novas imagens e adiciona ao produto
        if (arquivos != null) {
            for (MultipartFile arquivo : arquivos) {
                Imagem imagem = ProdutoFactory.criarImagem(arquivo, produto);
                if (imagem != null) {
                    produto.getImagens().add(imagem);
                }
            }
        }

        produtoService.salvar(produto);
        return "redirect:/produtosadmin";
    }



    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id).orElse(new Produto());
        model.addAttribute("produto", produto);
        return "produtos/form";
    }

    @PostMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtosadmin";
    }

}
