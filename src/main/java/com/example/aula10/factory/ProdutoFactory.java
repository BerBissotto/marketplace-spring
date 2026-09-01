package com.example.aula10.factory;

import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoFactory {

    public static Produto criarProduto(String nome, int quantidade, String descricao, String categoria, double preco,
                                       List<MultipartFile> arquivos) throws IOException {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setQuantidade(quantidade);
        produto.setDescricao(descricao);
        produto.setCategoria(categoria);
        produto.setPreco(preco);

        List<Imagem> imagens = new ArrayList<>();

        if (arquivos != null) {
            for (MultipartFile arquivo : arquivos) {
                Imagem imagem = criarImagem(arquivo, produto);
                if (imagem != null) {
                    imagens.add(imagem);
                }
            }
        }

        produto.setImagens(imagens); // adiciona lista ao produto

        return produto;
    }

    public static Imagem criarImagem(MultipartFile arquivo, Produto produto) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        Imagem imagem = new Imagem();
        imagem.setDados(arquivo.getBytes());
        imagem.setNome(arquivo.getOriginalFilename());
        imagem.setTipo(arquivo.getContentType() != null
                ? arquivo.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        imagem.setProduto(produto);
        return imagem;
    }
}
