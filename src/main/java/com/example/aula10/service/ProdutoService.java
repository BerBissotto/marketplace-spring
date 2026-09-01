package com.example.aula10.service;

import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;
import com.example.aula10.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.example.aula10.factory.ProdutoFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> buscarPorIds(List<Long> ids) {
        return produtoRepository.findAllById(ids);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> buscarComFiltros(String nome, Double valorMin, Double valorMax, String categoria) {
        return produtoRepository.buscarComFiltros(nome, valorMin, valorMax, categoria);
    }

    public Page<Produto> buscarComFiltros(String nome, Double valorMin, Double valorMax, String categoria, Pageable pageable) {
        return produtoRepository.buscarComFiltros(nome, valorMin, valorMax, categoria, pageable);
    }

    public Page<Produto> listarProdutos(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public Produto salvarComFactory(String nome, int quantidade, String descricao, String categoria, double preco, List<MultipartFile> arquivos) throws IOException {
        // Cria o produto sem imagens primeiro
        Produto produto = ProdutoFactory.criarProduto(nome, quantidade, descricao, categoria, preco, new ArrayList<>());

        // Cria as imagens a partir dos arquivos e associa ao produto
        if (arquivos != null) {
            for (MultipartFile arquivo : arquivos) {
                Imagem imagem = ProdutoFactory.criarImagem(arquivo, produto);
                if (imagem != null) {
                    produto.getImagens().add(imagem);
                }
            }
        }

        return salvar(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public Produto atualizar(Long id, Produto novoProduto) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(novoProduto.getNome());
            produto.setDescricao(novoProduto.getDescricao());
            produto.setPreco(novoProduto.getPreco());
            produto.setCategoria(novoProduto.getCategoria());
            produto.setQuantidade(novoProduto.getQuantidade());
            produto.setImagens(novoProduto.getImagens());
            return produtoRepository.save(produto);
        }).orElseGet(() -> {
            novoProduto.setId(id);
            return produtoRepository.save(novoProduto);
        });
    }
}
