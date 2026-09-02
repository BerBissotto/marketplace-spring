package com.example.aula10.dto;

import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;

import java.util.List;

public record ProdutoResponse(
        Long id,
        String nome,
        int quantidade,
        String descricao,
        String categoria,
        double preco,
        List<Long> imagemIds
) {
    public static ProdutoResponse from(Produto produto) {
        List<Long> imagens = produto.getImagens() == null
                ? List.of()
                : produto.getImagens().stream().map(Imagem::getId).toList();
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getQuantidade(),
                produto.getDescricao(), produto.getCategoria(), produto.getPreco(), imagens);
    }
}
