package com.example.aula10.controller;

import com.example.aula10.dto.ProdutoForm;
import com.example.aula10.dto.ProdutoResponse;
import com.example.aula10.factory.ProdutoFactory;
import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;
import com.example.aula10.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("produto", new ProdutoForm());
        return "produtos/form";
    }

    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarProdutos());
        return "produtos/lista";
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ProdutoResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/novo")
    public String salvarProdutoComFactory(
            @Valid @ModelAttribute("produto") ProdutoForm form,
            BindingResult result,
            @RequestParam(value = "imagens", required = false) List<MultipartFile> arquivos,
            Model model) {

        if (result.hasErrors()) {
            return "produtos/form";
        }

        try {
            Produto produto = ProdutoFactory.criarProduto(form.getNome(), form.getQuantidade(),
                    form.getDescricao(), form.getCategoria(), form.getPreco(), arquivos);
            produtoService.salvar(produto);
            return "redirect:/produtosadmin";
        } catch (IOException | IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            return "produtos/form";
        }
    }

    @PostMapping("/atualizar")
    public String atualizarProduto(
            @Valid @ModelAttribute("produto") ProdutoForm form,
            BindingResult result,
            @RequestParam(value = "imagens", required = false) List<MultipartFile> arquivos,
            Model model) {

        if (form.getId() == null) {
            result.reject("produto.id", "Produto inválido.");
        }
        if (result.hasErrors()) {
            adicionarImagensExistentes(form.getId(), model);
            return "produtos/form";
        }

        Produto produto = produtoService.buscarPorId(form.getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(form.getNome());
        produto.setDescricao(form.getDescricao());
        produto.setQuantidade(form.getQuantidade());
        produto.setCategoria(form.getCategoria());
        produto.setPreco(form.getPreco());

        try {
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
        } catch (IOException | IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            adicionarImagensExistentes(form.getId(), model);
            return "produtos/form";
        }
    }



    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        model.addAttribute("produto", ProdutoForm.from(produto));
        model.addAttribute("imagensExistentes", produto.getImagens());
        return "produtos/form";
    }

    @PostMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtosadmin";
    }

    private void adicionarImagensExistentes(Long id, Model model) {
        if (id != null) {
            produtoService.buscarPorId(id)
                    .ifPresent(produto -> model.addAttribute("imagensExistentes", produto.getImagens()));
        }
    }

}
